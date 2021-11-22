# Build script used to setup Kevlar.

import os

# Build premake5 used to build the repository.
os.system("cd \"ThirdParty/premake5\" && make -f Bootstrap.mak linux")

# Build the makefiles for the projects.
os.system("call \"ThirdParty/premake5/bin/release/premake5\" gmake2")