---------- Kevlar Application project description ----------

project "Kevlar"
	language "C++"
	kind "ConsoleApp"
	systemversion "latest"
	cppdialect "C++17"
	staticruntime "on"

	flags { "MultiProcessorCompile" }

	targetdir "%{wks.location}/Builds/Binaries/%{cfg.longname}"
	objdir "%{wks.location}/Builds/Intermediate/%{cfg.longname}"

	files {
		"**.txt",
		"**.cpp",
		"**.hpp",
		"**.lua",
		"**.txt",
		"**.md",
	}

	includedirs {
		"%{wks.location}/Application/",
		"%{wks.location}/Driver/",
	}

	libdirs {
	}

	links { 
	}
