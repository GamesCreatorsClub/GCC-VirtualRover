import threading
import cwiid


class WiiMote:
    def __init__(self):
        self.ir_list = [[False, 0, 0, 0], [False, 0, 0, 0], [False, 0, 0, 0]]

        self.max_coors = [0, 0]
        self.wiimote = None
        self.wiimote_thread = None
        self.connected = False
        self.initialising = False
        self.initialised = False
        self.loop_callback = None

    def start_connecting(self, loop_callback=None):
        self.loop_callback = loop_callback
        self.wiimote_thread = threading.Thread(target=self.try_to_connect, daemon=True)
        self.wiimote_thread.start()

    def callback(self, mesg_list, time):
        for mesg in mesg_list:
            if mesg[0] == cwiid.MESG_STATUS:
                pass
            elif mesg[0] == cwiid.MESG_BTN:
                pass
            elif mesg[0] == cwiid.MESG_ACC:
                pass
            elif mesg[0] == cwiid.MESG_IR:
                i = 0
                for src in mesg[1]:
                    if src and i < 3:
                        self.ir_list[i][0] = True
                        pos = src['pos']
                        self.ir_list[i][1] = pos[0]
                        self.ir_list[i][2] = pos[1]
                        self.ir_list[i][3] = src['size']
                        i += 1
                        new_max = False
                        if pos[0] > self.max_coors[0]:
                            self.max_coors[0] = pos[0]
                            new_max = True
                        if pos[1] > self.max_coors[1]:
                            self.max_coors[1] = pos[1]
                            new_max = True
                        # if new_max:
                        #     print(f"Got new max {self.max_coors}")
                while i < 3:
                    self.ir_list[i][0] = False
                    i += 1
                # if len(mesg[1]) > 0:
                #     print(mesg[1][0])
            elif mesg[0] == cwiid.MESG_NUNCHUK:
                pass
            elif mesg[0] == cwiid.MESG_CLASSIC:
                pass
            elif mesg[0] ==  cwiid.MESG_BALANCE:
                pass
            elif mesg[0] == cwiid.MESG_MOTIONPLUS:
                pass
            elif mesg[0] ==  cwiid.MESG_ERROR:
                print("Error message received")
                self.wiimote.close()
                exit(-1)
            else:
                print('Unknown Report')

    def init_wiimote(self):
        self.initialising = True
        print("Initialising...")
        self.wiimote.mesg_callback = self.callback
        self.wiimote.led = cwiid.LED2_ON

        rpt_mode = 0
        rpt_mode ^= cwiid.RPT_IR
        self.wiimote.rpt_mode = rpt_mode
        self.wiimote.enable(cwiid.FLAG_MESG_IFC)
        print("Initialising... done")
        self.initialised = True

    def try_to_connect(self):
        while not self.connected:
            try:
                self.wiimote = cwiid.Wiimote()
                print("Connected...")
                self.connected = True
            except RuntimeError as e:
                print("Failed to connect... " + str(e))
                if self.loop_callback is not None:
                    callback_direction = self.loop_callback()
                    if callback_direction is not None and not callback_direction:
                        return
