

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid;

/**
* The commands in the Admin Command Group provide high-level test functions and control parameters.
*
* The Admin Command Group contains the following commands:
* <ul>
*   <li>echo: Echo the parameter text.
*   <li>shutdown: Terminate the bridge.
*   <li>noOp: No operation command.
*   <li>setPositionSamplePublishRate: Publish Agent pose at given rate.
*   <li>positionSamplePublishRate: Get Agent pose publication rate.
*   <li>setJointSamplePublishRate: Publish Agent articulation at given rate.
*   <li>jointSampleRate: Get Agent articulation publication rate.
* </ul>
*/
/**  Echos the parameter text. */
/** Key for parameter used in reply by bridge. */
/** Terminate the bridge. */
/** No operation command. */
/** Publish Agent pose at given rate. */
/** In Hz. */
/** Publish Agent articulation at given rate. */
/** Publish Agent articulation at given rate. */
/** Publish Agent articulation at given rate. */
/** Publish Agent articulation at given rate. */
/**
* The commands in the ImageSensor Command Group are used to capture images from Agent-mounted cameras.
*
* The ImageSensor Command Group contains the following commands:
* <ul>
*   <li>reqSensorState:
*   <li>imageAcquire:
* </ul>
*/
/** ImageSensorState message. */
/** Message. */
/** Message. */
/** Send image acquire command with this command, the ImageCommand attribute will be populated. */
/** IMAGESENSOR_CAMERA_TYPE_xxx. */
/** MIMETypeConstants. */
/** IMAGESENSOR_COLOR_TYPE_XXX */
/** IMAGESENSOR_CAPTURE_XXX */
/**
* The commands in the Mobility Command Group controls the motion of navigable rovers over a surface.
*
* The Mobility Command Group contains the following commands:
* <ul>
*   <li>simpleMove:
*   <li>simpleMove6DOF:
*   <li>move:
*   <li>move6DOF
* </ul>
*/
/** Stops all motion of the Agent, joints, wheels, etc. */
/**  Can be defined as a rotation about an axis. */
/**
* The x,y,theta tolerance for waypoint. Theta is specified in radians. For a non-directional waypoint, use
* negative or > PI value.
*/
/** In meters/sec. */
/**  In meters/sec. */
/** Navigation Algorithm for MobilityCommand. */
/** Frame information for MobilityCommand and CameraCommand. */
/**
* The commands in the Queue command group control task execution within the Sequencer.
*
* The Queue Command Group contains the following commands:
* <ul>
*   <li>cancelCurrentTask:
*   <li>cancelAll:
*   <li>deleteAll:
*   <li>suspendOnCompletion:
*   <li>suspend:
*   <li>resume:
*   <li>loadMacro:
* </ul>
*/
/** Abort all active commands (as possible). */
/** Mark all commands as canceled. */
/** Clear all commands. */
/** Complete currently executed command and suspend queue execution. */

public class QUEUE_METHOD_SUSPENDONCOMPLETE {    

    public static final String VALUE = "suspendOnCompletion";
}