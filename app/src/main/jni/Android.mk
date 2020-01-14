LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_C_INCLUDES := $(LOCAL_PATH)/
APP_ABI := arm64-v8a armeabi-v7a x86_64
# APP_OPTIM=release
LOCAL_MODULE := AiKuon
LOCAL_SRC_FILES := JniFunc.cpp stdFunc.cpp Ai_kuon.cpp
include $(BUILD_SHARED_LIBRARY)