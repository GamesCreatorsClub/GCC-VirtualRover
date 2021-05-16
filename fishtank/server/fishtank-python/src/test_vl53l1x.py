# import sys
# sys.path.insert(0, "build/lib.linux-armv7l-2.7/")

from VL53L1X import VL53L1X, VL53L1xUserRoi, VL53L1xDistanceMode
import time
import RPi.GPIO as GPIO

GPIO.setwarnings(False)
GPIO.setmode(GPIO.BCM)
GPIO.setup(4, GPIO.OUT)
GPIO.output(4, 0)


def setup_tof(address: int) -> VL53L1X:
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


tof1 = setup_tof(0x30)
print("")
GPIO.output(4, 1)
tof2 = setup_tof(0x31)


tof1.start_ranging(VL53L1xDistanceMode.SHORT)
tof2.start_ranging(VL53L1xDistanceMode.SHORT)

distances = [[], [], [], [], [], []]


print("\033[2J")
try:
    while True:
        now = time.time()
        distance1_mm = tof1.get_distance()
        distance2_mm = tof2.get_distance()

        distances[0].append(str(distance1_mm) if distance1_mm > 0 else "    --")
        distances[1].append(str(distance2_mm) if distance2_mm > 0 else "    --")
        distances[2].append(f"{time.time() - now:.03f}")
        distances[4].append(distance1_mm)
        distances[5].append(distance2_mm)

        avg1 = sum(distances[4]) / len(distances[0])
        avg2 = sum(distances[5]) / len(distances[1])

        detected = "             "
        if distances[4][-1] < avg1 * 0.9 or distances[5][-1] < avg2 * 0.9:
            detected = ", DETECTED!!!"

        distances[3].append(detected)

        print("\033[0;0H")
        for i in range(len(distances[0])):
            print(f"{distances[0][i]:>6}/{distances[1][i]:>6} took {distances[2][i]}s{distances[3][i]}")
        # print(f"{distance1_mm:06} took {time.time() - now:.03f}s")
        time.sleep(0.001)
        if len(distances[0]) > 35:
            for distance in distances:
                del distance[0]
except KeyboardInterrupt:
    tof1.stop_ranging()
    tof2.stop_ranging()
    tof1.close()
    tof2.close()
