-- Project configuration file for Kevlar.

workspace "Kevlar"
	architecture "x64"

	configurations {
		"Debug",
		"PreRelease",
		"Release",
	}

	-- Libraries
	IncludeDir = {}

	-- Binary includes directories
	IncludeLib = {}

	-- Binaries
	Binary = {}

	filter "configurations:Debug"
		defines { "KEVLAR_DEBUG" }
		symbols "On"
		runtime "Debug"

	filter "configurations:PreRelease"
		defines { "KEVLAR_PRE_RELEASE" }
		optimize "On"
		runtime "Release"

	filter "configurations:Release"
		defines { "KEVLAR_RELEASE" }
		optimize "On"
		runtime "Release"

    include "Application/Application.lua"
    include "Driver/Driver.lua"
