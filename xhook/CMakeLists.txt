cmake_minimum_required(VERSION 3.18.1)
project(libxhook)

set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/build/intermediates/native_deps/${CMAKE_BUILD_TYPE}/${ANDROID_ABI})

set(xhook_source_dir src/main/cpp)

set(
        xhook_source
        ${xhook_source_dir}/xh_core.c
        ${xhook_source_dir}/xh_elf.c
        ${xhook_source_dir}/xh_jni.c
        ${xhook_source_dir}/xh_log.c
        ${xhook_source_dir}/xh_util.c
        ${xhook_source_dir}/xh_version.c
        ${xhook_source_dir}/xhook.c
)

add_library(xhook
        SHARED
        ${xhook_source})

find_library(
        log-lib
        log)

target_link_libraries(
        xhook
        PUBLIC ${log-lib}
)

target_include_directories(
        xhook
        PUBLIC ${xhook_source_dir}
)