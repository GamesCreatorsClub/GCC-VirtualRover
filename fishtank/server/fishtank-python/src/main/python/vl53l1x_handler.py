# import sys
# sys.path.insert(0, "build/lib.linux-armv7l-2.7/")
import threading
import time

import RPi.GPIO as GPIO
from VL53L1X import VL53L1X, VL53L1xUserRoi, VL53L1xDistanceMode


class VL53L1X_Handler:
    def __init__(self, trigger_change_callback):
        self._distances = [[], [], [], [], [], []]
        self._previous_detected = False
        self._trigger_change_callback = trigger_change_callback
        self._tof1 = None
        self._tof2 = None
        self._thread = None
        self.setup_gpio()
        self.setup_sensors()

    def setup_gpio(self):
        GPIO.setwarnings(False)

        GPIO.setmode(GPIO.BCM)
        GPIO.setup(4, GPIO.OUT)
        GPIO.output(4, 0)

    def _setup_tof(self, address: int) -> VL53L1X:
        print(f"Setting up tof on {hex(address)}")
        print("    Trying tof on original address 0x29...")
        try:
            tof = VL53L1X(i2c_bus=1, i2c_address=0x29)
            tof.open()
            print(f"    Found tof on original address 0x29, moving it to {hex(address)}")
            tof.change_address(address)
            tof.close()
            tof.open()
            print(f"    Moved tof to {hex(address)}")
        except RuntimeError:
            print(f"    Failed to find tof on original address 0x29, trying new address {hex(address)}")
            tof = VL53L1X(i2c_bus=1, i2c_address=address)
            tof.open()
            print(f"    Found tof to {hex(address)}")
        tof.set_user_roi(VL53L1xUserRoi(0, 7, 15, 8))
        tof.set_timing(50, 70)

        return tof

    def setup_sensors(self):
        self._tof1 = self._setup_tof(0x30)
        print("")
        GPIO.output(4, 1)
        self._tof2 = self._setup_tof(0x31)

        self._tof1.start_ranging(VL53L1xDistanceMode.SHORT)
        self._tof2.start_ranging(VL53L1xDistanceMode.SHORT)

    def start(self):
        self._thread = threading.Thread(target=self.range(), daemon=True)
        self._thread.start()

    def range(self):
        try:
            while True:
                now = time.time()
                distance1_mm = self._tof1.get_distance()
                distance2_mm = self._tof2.get_distance()

                self._distances[0].append(str(distance1_mm) if distance1_mm > 0 else "    --")
                self._distances[1].append(str(distance2_mm) if distance2_mm > 0 else "    --")
                self._distances[2].append(f"{time.time() - now:.03f}")
                self._distances[4].append(distance1_mm)
                self._distances[5].append(distance2_mm)

                avg1 = sum(self._distances[4]) / len(self._distances[0])
                avg2 = sum(self._distances[5]) / len(self._distances[1])

                detected = False
                if self._distances[4][-1] < avg1 * 0.9 or self._distances[5][-1] < avg2 * 0.9:
                    detected = True

                self._distances[3].append(detected)

                if self._previous_detected != detected:
                    self._trigger_change_callback(detected)

                self._previous_detected = detected

                time.sleep(0.001)
                if len(self._distances[0]) > 35:
                    for distance in self._distances:
                        del distance[0]
        finally:
            self._tof1.stop_ranging()
            self._tof2.stop_ranging()
            self._tof1.close()
            self._tof2.close()
