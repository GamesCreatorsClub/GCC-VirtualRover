#!/usr/bin/env python3

################################################################################
# Copyright (C) 2020 Abstract Horizon
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Apache License v2.0
# which accompanies this distribution, and is available at
# https://www.apache.org/licenses/LICENSE-2.0
#
#  Contributors:
#    Daniel Sendula - initial API and implementation
#
#################################################################################

import datetime
import glob
import os
from os.path import join
from zipfile import ZipFile, ZIP_DEFLATED


CURRENT_PATH = os.path.dirname(os.path.abspath(__file__))

SOURCE_PATHS = [join(CURRENT_PATH, "src/main"), join(CURRENT_PATH, "src/resources")]  # List of your source directories

# Remove 'src/resources' from above list if you don't need them.

RESULT_NAME = "put-your-project-name-here"  # CHANGE: your project name - name of resulting executable
MAIN_FILE = "put-your-main-file-name-here"  # CHANGE: Main python file without '.py' suffix (that will be invoked)

REQUIREMENTS_FILE = join(CURRENT_PATH, "requirements.txt")  # Requirements file to package with your source (no need to change)

TARGET_PATH = join(CURRENT_PATH, "target")  # working directory (no need to change)
TARGET_REQUIREMENTS_PATH = join(TARGET_PATH, "requirements")  # dir where install dependencies before being packaged (no need to change)
TARGET_TEMPLATES_PATH = join(TARGET_PATH, "templates")  # dir where templated files are going to be stored before being packaged (no need to change)
VERSION_FILE = join(CURRENT_PATH, "VERSION")  # version file (no need to change)

# Version file here is just for your convenience. Check below what you can comment out if you don't want to maintain version


def ensure_empty_dir(path):
    if not os.path.exists(path):
        os.makedirs(path)

    def del_recursively(path_to_delete):
        for file in os.listdir(path_to_delete):
            new_path = os.path.join(path_to_delete, file)
            try:
                if os.path.isdir(new_path):
                    del_recursively(new_path)
                    os.rmdir(new_path)
                else:
                    os.remove(new_path)
            except IOError:
                pass

    del_recursively(path)

    return path


def list_all_paths(dir_name):
    paths = []

    for root, directories, files in os.walk(dir_name):
        for filename in files:
            source_filename = os.path.join(root, filename)
            dest_filename = os.path.join(root[len(dir_name) + 1:] , filename)
            paths.append((source_filename, dest_filename))

    return paths


def install_requirements(requirements_file, target_directory):
    print(f"Attempting to install requirements from '{requirements_file}' in '{target_directory}")
    os.system(f"pip install --platform=aarch64 -r {glob.glob(requirements_file)[0]} --target {target_directory}")


def update_build_version(version_file, target_templates_path):
    if os.path.exists(version_file):
        ensure_empty_dir(target_templates_path)
        with open(version_file, 'r') as in_file:
            lines = in_file.readlines()
            lines[0] = lines[0] + datetime.datetime.now().strftime('-%Y%m%d%H%M%S')
            with open(os.path.join(target_templates_path, os.path.split(version_file)[1]), 'w') as out_file:
                out_file.write("\n".join(lines) + "\n")


def create_zipfile(zip_file, source_paths, *other_paths):
    with ZipFile(zip_file, 'w', ZIP_DEFLATED) as zip_file:
        for source_path in source_paths:
            for source_file, result_file in list_all_paths(source_path):
                zip_file.write(source_file, result_file)

        for path in other_paths:
            for source_file, result_file in list_all_paths(path):
                zip_file.write(source_file, result_file)

    return zip_file


START_SCRIPT = f"""#!/bin/bash

export SCRIPT_FILE=`which "$0" 2>/dev/null`

source=`cat <<EOF
import sys
import os
import runpy

sys.argv[0] = os.environ['SCRIPT_FILE']
sys.path.insert(0, sys.argv[0])

runpy.run_module("{MAIN_FILE}", run_name="__main__")
EOF`

/usr/bin/env python3 -u -c "${{source}}" $@
exit $?
"""

if __name__ == "__main__":

    ensure_empty_dir(TARGET_PATH)
    ensure_empty_dir(TARGET_REQUIREMENTS_PATH)

    zip_file = os.path.join(TARGET_PATH, "out.zip")
    result_executable = os.path.join(TARGET_PATH, RESULT_NAME)

    # Comment out or remove if no requirements file is needed
    install_requirements(REQUIREMENTS_FILE, target_directory=TARGET_REQUIREMENTS_PATH)

    # Comment out or remove if you don't need version file
    update_build_version(VERSION_FILE, TARGET_TEMPLATES_PATH)

    create_zipfile(zip_file, SOURCE_PATHS, TARGET_REQUIREMENTS_PATH, TARGET_TEMPLATES_PATH)

    with open(result_executable, "wb") as f:
        f.write(START_SCRIPT.encode("utf-8"))
        with open(zip_file, "rb") as zip_file:
            f.write(zip_file.read())

    os.system(f"chmod u+x '{result_executable}'")
