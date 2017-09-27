LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := serial_port
LOCAL_LDLIBS := \
	-llog \

LOCAL_SRC_FILES := \
	E:\SerialPortTest\app\src\main\jni\Android.mk \
	E:\SerialPortTest\app\src\main\jni\SerialPort.c \
	E:\SerialPortTest\app\src\main\jni\util.c \

LOCAL_C_INCLUDES += E:\SerialPortTest\app\src\main\jni
LOCAL_C_INCLUDES += E:\SerialPortTest\app\src\release\jni

include $(BUILD_SHARED_LIBRARY)
