# Build script used to setup Kevlar.

import os

"""
Build and run using the included third party premake library source.
"""
def build_and_run_premake():
	# Build premake5 used to build the repository.
	os.system("cd \"ThirdParty/premake5\" && make -f Bootstrap.mak linux")

	# Build the makefiles for the projects.
	os.system("\"ThirdParty/premake5/bin/release/premake5\" gmake2")

if os.system("command -v premake5") > 0:
	build_and_run_premake()

else:
	os.system("premake5 gmake2")