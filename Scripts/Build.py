# Build script used to setup Kevlar.

import os

# Build the makefiles for the projects.
os.system("cd .. && call \"Tools/premake5\" gmake2")