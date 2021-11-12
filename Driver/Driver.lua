---------- Kevlar Driver project description ----------

project "KevlarDriver"
	language "C++"
	kind "Makefile"
	systemversion "latest"
	cppdialect "C++17"
	staticruntime "on"

	buildcommands {
        "make",
    }
    
    rebuildcommands {
        "make rebuild",
    }
    
    cleancommands {
        "make clean",
    }
