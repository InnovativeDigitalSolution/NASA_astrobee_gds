

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.astrobee;

/** Grab control of an agent */
/** Initialize bias. */
/** Command used to load a nodelet in the system. Doesn't work with nodes running on the HLP. */
/** Type of nodelet (namespace/classname). The type is specified in the system monitor config file so you may not need to specify this. */
/** Name of nodelet manager. This should be unnecessary since the system monitor should have received a heartbeat at startup from the node and the nodelet manager name is in the heartbeat. The system monitor saves it and should be able to use it to load nodelets. If commands fails, you may want to try to specify it. */
/** Can be left blank. */
/** Reset ekf. */
/** This command is used to switch between localization pipelines. */
/** Specify which pipeline to switch to. */
/** Command used to unload a nodelet in the system. Doesn't work with nodes running on the HLP. With great power comes great responsibility! Don't unload a nodelet crucial to the system!! */
/** This should be unnecessary since the system monitor should have received a heartbeat at startup from the node and the heartbeat contains thenodelet manager name. If the command fails, you may want to try to specify it. */
/** This command is used to unterminate the robot. It will only reset the terminate flag but will not start up the pmcs or repower the payloads. */
/** This command wakes astrobee from a hibernated state into a nominal state. */
/** This command wakes astrobee from a hibernated state into a safe state. */
/** Erases everything on the hlp. */
/** Move arm while perched to control camera angle */
/** Whether to perform a pan, tilt, or both. */
/** Open or close gripper */
/** Clear data */
/** Start downloading data */
/** Set data-to-disk configuration to be the data-to-disk file most recently uplinked; the file specifies which data to save to free flyer onboard storage, and at what rates */
/** Starts the recording of the topics configured with the set data to disk command. */
/** Stop downloading data */
/** Stops the recording of the topics configured with the set data to disk command. */
/** Pass data to guest science APK */
/** Specify which guest science APK to send the data to */
/** The data to send (e.g. could be JSON-encoded data structure) */
/** Start guest science APK */
/** Specify which guest science APK to start */
/** Terminate guest science APK */
/** Specify which guest science APK to terminate */
/** Dock Astrobee. Must meet dock approach preconditions (positioned at dock approach point, etc). */
/** Berth number can only be 1 or 2. */
/** Stop propulsion impeller motors */
/** It takes the prop modules a couple of seconds to ramp up to the requested flight mode if not already at that flight mode. This command ramps up the prop modules so that when a move command is issued, it will execute right away. This doesn't need to be used for nominal use but may be used/needed for astrobee synchronization. */
/** Undock Astrobee */
/** Pause the running plan */
/** Run the loaded plan */
/** Set active plan to be the plan file that was most recently uploaded */
/** Skip next trajectory or command in the plan */
/** Pause plan for specified duration. Do nothing if docked/perched, otherwise station keep. */
/** seconds to pause */
/** Power off an item within Astrobee */
/** Any component within Astrobee that can be turned on or off. */
/** Power on an item within Astrobee */
/** Any component within Astrobee that can be turned on or off. */
/** Generic command used to make up a command after the Control Station freeze. */
/** Set camera parameters. */
/** Camera name */
/** Desired camera mode. Either streaming or recording. */
/** Desired frame size in pixels. */
/** Applies to both modes of camera. */
/** Only for sci camera; related to quality, may change name to bitrate. */
/** Set camera to record video. */
/** Camera name */
/** Record camera video. */
/** Set streaming camera video to the ground. */
/** Camera name */
/** Send live video to the ground. */
/** Command to turn on and off the obstacle detector */
/** Command to turn on and off checking keepout zones */
public class SETTINGS_METHOD_SET_CHECK_ZONES_DTYPE_CHECK_ZONES {    

    public static final rapid.DataType VALUE = (rapid.DataType.RAPID_BOOL);
}
