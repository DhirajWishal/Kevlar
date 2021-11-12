---------- Kevlar Driver project description ----------

project "KevlarDriver"
	language "C++"
	kind "Makefile"
	systemversion "latest"
	cppdialect "C++17"
	staticruntime "on"

	flags { "MultiProcessorCompile" }

	targetdir "%{wks.location}/Builds/Binaries/%{cfg.longname}"
	objdir "%{wks.location}/Builds/Intermediate/%{cfg.longname}"

	buildcommands {
        "cd Builder && make",
    }
    
    rebuildcommands {
        "cd Builder && make rebuild",
    }
    
    cleancommands {
        "cd Builder && make clean",
    }
	
	files {
		"**.txt",
		"**.c",
		"**.h",
		"**.cpp",
		"**.hpp",
		"**.lua",
		"**.txt",
		"**.md",
	}

	includedirs {
		"%{wks.location}/Driver/",
	}
