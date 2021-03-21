import os
import select
import subprocess
import threading


class Visualisation():
    def __init__(self):
        self._x = 0
        self._y = 80
        self._width = 800
        self._height = 600
        self._tcp = True
        self._mute = True
        self._server_address = "127.0.0.1:7454"
        self._java_executable = "java"
        self._process = None
        self._thread = None
        self._debug = False
        self._remote_java_debugging = False

        if os.getcwd().endswith("/src"):
            self._jar_path = "../libs/piwars-simulator.jar"
        else:
            self._jar_path = "libs/piwars-simulator.jar"

    def set_position(self, x, y):
        self._x = x
        self._y = y

    def get_position(self):
        return self._x, self._y

    def set_size(self, width, height):
        self._width = width
        self._height = height

    def get_size(self):
        return self._width, self._height

    def set_tcp(self, tcp):
        self._tcp = tcp

    def is_tcp(self):
        return self._tcp

    def set_mute(self, mute):
        self._mute = mute

    def is_mute(self):
        return self._mute

    def set_jar_path(self, path):
        self._jar_path = path

    def get_jar_path(self):
        return self._jar_path

    def set_java_executable(self, java_executable):
        self._java_executable = java_executable

    def get_java_executable(self):
        return self._java_executable

    def set_server_address(self, server_address):
        self._server_address = server_address

    def get_server_address(self):
        return self._server_address

    def set_debug(self, debug):
        self._debug = debug

    def is_debug(self):
        return self._debug

    def set_remote_java_debugging(self, remote_java_debugging):
        self._remote_java_debugging = remote_java_debugging

    def is_remote_java_debugging(self):
        return self._remote_java_debugging

    def get_thread(self):
        return self._thread

    def get_process(self):
        return self._process

    def start(self):
        self._thread = threading.Thread(target=self.run, daemon=True)
        self._thread.start()

    def stop(self):
        if self._process is not None:
            self._process.kill()

    def run(self):
        command = [self.get_java_executable()]
        command += ["-XstartOnFirstThread"]
        if self.is_remote_java_debugging():
            command += ["-Xdebug", "-Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=y"]

        command += ["-jar", self.get_jar_path()]

        if self.is_mute():
            command += ["--mute"]

        command += ["--position", str(self._x) + "x" + str(self._y)]
        command += ["--resolution", str(self._width) + "x" + str(self._height)]
        command += ["--simulation"]

        if self.is_tcp():
            command += ["--server", "tcp:" + self.get_server_address()]
        else:
            command += ["--server", "udp:" + self.get_server_address()]

        if self.is_debug():
            command += ["--debug"]

        print("Current dir is " + os.getcwd())

        self._process = subprocess.Popen(command,
                                   env=os.environ,
                                   bufsize=0,
                                   stdout=subprocess.PIPE,
                                   stderr=subprocess.PIPE,
                                   shell=False,
                                   universal_newlines=True,
                                   cwd=os.getcwd())

        self._process.poll()

        while self._process.returncode is None:
            reads = [self._process.stdout.fileno(), self._process.stderr.fileno()]
            ret = select.select(reads, [], [])

            for fd in ret[0]:
                if fd == self._process.stdout.fileno():
                    line = self._process.stdout.readline()
                    print(line)
                if fd == self._process.stderr.fileno():
                    line = self._process.stderr.readline()
                    print(line)

            self._process.poll()

        for line in self._process.stdout.readlines():
            if len(line) > 0:
                print(line)
        for line in self._process.stderr.readlines():
            if len(line) > 0:
                print(line)
