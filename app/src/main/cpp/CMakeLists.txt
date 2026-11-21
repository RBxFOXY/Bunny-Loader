
cmake_minimum_required(VERSION 3.22)
project(bunny CXX C ASM)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_VISIBILITY_PRESET hidden)

find_package(shadowhook REQUIRED CONFIG)

file(GLOB_RECURSE BL_SOURCES CONFIGURE_DEPENDS
    ${CMAKE_CURRENT_SOURCE_DIR}/*.cpp
)
list(FILTER BL_SOURCES EXCLUDE REGEX "/compile/")

file(GLOB_RECURSE BL_ASM_SOURCES CONFIGURE_DEPENDS
    ${CMAKE_CURRENT_SOURCE_DIR}/*.S
)

if(ANDROID_ABI STREQUAL "armeabi-v7a")

    list(FILTER BL_SOURCES EXCLUDE REGEX "/CodePatch\\.cpp$")
    list(FILTER BL_SOURCES EXCLUDE REGEX "/DotnetCall\\.cpp$")
    list(FILTER BL_SOURCES EXCLUDE REGEX "/GuardedCall\\.cpp$")

    list(FILTER BL_ASM_SOURCES EXCLUDE REGEX ".*")

    add_compile_definitions(BL_ARM32=1)

elseif(ANDROID_ABI STREQUAL "arm64-v8a")

    list(FILTER BL_SOURCES EXCLUDE REGEX "/CodePatchARM32\\.cpp$")
    list(FILTER BL_SOURCES EXCLUDE REGEX "/DotnetCallARM32\\.cpp$")

    add_compile_definitions(BL_ARM64=1)

else()
    message(FATAL_ERROR "Unsupported ABI: ${ANDROID_ABI}")
endif()

add_library(blcompile SHARED compile/CompilerHost.cpp)
target_link_libraries(blcompile log dl)

add_library(bunny SHARED ${BL_SOURCES} ${BL_ASM_SOURCES})

target_include_directories(bunny PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}
    ${CMAKE_CURRENT_BINARY_DIR}/gen
)

target_compile_options(bunny PRIVATE
    -O2
    -fno-exceptions
    -fno-rtti
)

target_link_libraries(bunny shadowhook::shadowhook log dl z)
