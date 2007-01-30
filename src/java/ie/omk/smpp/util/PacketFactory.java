package ie.omk.smpp.util;

import ie.omk.smpp.BadCommandIDException;
import ie.omk.smpp.message.AlertNotification;
import ie.omk.smpp.message.BindReceiver;
import ie.omk.smpp.message.BindReceiverResp;
import ie.omk.smpp.message.BindTransceiver;
import ie.omk.smpp.message.BindTransceiverResp;
import ie.omk.smpp.message.BindTransmitter;
import ie.omk.smpp.message.BindTransmitterResp;
import ie.omk.smpp.message.CancelSM;
import ie.omk.smpp.message.CancelSMResp;
import ie.omk.smpp.message.DataSM;
import ie.omk.smpp.message.DataSMResp;
import ie.omk.smpp.message.DeliverSM;
import ie.omk.smpp.message.DeliverSMResp;
import ie.omk.smpp.message.EnquireLink;
import ie.omk.smpp.message.EnquireLinkResp;
import ie.omk.smpp.message.GenericNack;
import ie.omk.smpp.message.Outbind;
import ie.omk.smpp.message.ParamRetrieve;
import ie.omk.smpp.message.ParamRetrieveResp;
import ie.omk.smpp.message.QueryLastMsgs;
import ie.omk.smpp.message.QueryLastMsgsResp;
import ie.omk.smpp.message.QueryMsgDetails;
import ie.omk.smpp.message.QueryMsgDetailsResp;
import ie.omk.smpp.message.QuerySM;
import ie.omk.smpp.message.QuerySMResp;
import ie.omk.smpp.message.ReplaceSM;
import ie.omk.smpp.message.ReplaceSMResp;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.message.SubmitMulti;
import ie.omk.smpp.message.SubmitMultiResp;
import ie.omk.smpp.message.SubmitSM;
import ie.omk.smpp.message.SubmitSMResp;
import ie.omk.smpp.message.Unbind;
import ie.omk.smpp.message.UnbindResp;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory class for SMPP packets.
 * @version $Id$
 */
public final class PacketFactory {
    private static final Logger LOG = LoggerFactory.getLogger(PacketFactory.class);
    private static final PacketFactory INSTANCE = new PacketFactory();
    
    private final Map<Integer, Class<? extends SMPPPacket>> commands;
    private final Map<Integer, Class<? extends SMPPPacket>> userCommands =
        new HashMap<Integer, Class<? extends SMPPPacket>>();
    
    private PacketFactory() {
        Map<Integer, Class<? extends SMPPPacket>> commands =
            new HashMap<Integer, Class<? extends SMPPPacket>>();
        commands.put(Integer.valueOf(SMPPPacket.ALERT_NOTIFICATION), AlertNotification.class);
        commands.put(Integer.valueOf(SMPPPacket.BIND_RECEIVER), BindReceiver.class);
        commands.put(Integer.valueOf(SMPPPacket.BIND_RECEIVER_RESP), BindReceiverResp.class);
        commands.put(Integer.valueOf(SMPPPacket.BIND_TRANSCEIVER), BindTransceiver.class);
        commands.put(Integer.valueOf(SMPPPacket.BIND_TRANSCEIVER_RESP), BindTransceiverResp.class);
        commands.put(Integer.valueOf(SMPPPacket.BIND_TRANSMITTER), BindTransmitter.class);
        commands.put(Integer.valueOf(SMPPPacket.BIND_TRANSMITTER_RESP), BindTransmitterResp.class);
        commands.put(Integer.valueOf(SMPPPacket.CANCEL_SM), CancelSM.class);
        commands.put(Integer.valueOf(SMPPPacket.CANCEL_SM_RESP), CancelSMResp.class);
        commands.put(Integer.valueOf(SMPPPacket.DATA_SM), DataSM.class);
        commands.put(Integer.valueOf(SMPPPacket.DATA_SM_RESP), DataSMResp.class);
        commands.put(Integer.valueOf(SMPPPacket.DELIVER_SM), DeliverSM.class);
        commands.put(Integer.valueOf(SMPPPacket.DELIVER_SM_RESP), DeliverSMResp.class);
        commands.put(Integer.valueOf(SMPPPacket.ENQUIRE_LINK), EnquireLink.class);
        commands.put(Integer.valueOf(SMPPPacket.ENQUIRE_LINK_RESP), EnquireLinkResp.class);
        commands.put(Integer.valueOf(SMPPPacket.GENERIC_NACK), GenericNack.class);
        commands.put(Integer.valueOf(SMPPPacket.OUTBIND), Outbind.class);
        commands.put(Integer.valueOf(SMPPPacket.PARAM_RETRIEVE), ParamRetrieve.class);
        commands.put(Integer.valueOf(SMPPPacket.PARAM_RETRIEVE_RESP), ParamRetrieveResp.class);
        commands.put(Integer.valueOf(SMPPPacket.QUERY_LAST_MSGS), QueryLastMsgs.class);
        commands.put(Integer.valueOf(SMPPPacket.QUERY_LAST_MSGS_RESP), QueryLastMsgsResp.class);
        commands.put(Integer.valueOf(SMPPPacket.QUERY_MSG_DETAILS), QueryMsgDetails.class);
        commands.put(Integer.valueOf(SMPPPacket.QUERY_MSG_DETAILS_RESP), QueryMsgDetailsResp.class);
        commands.put(Integer.valueOf(SMPPPacket.QUERY_SM), QuerySM.class);
        commands.put(Integer.valueOf(SMPPPacket.QUERY_SM_RESP), QuerySMResp.class);
        commands.put(Integer.valueOf(SMPPPacket.REPLACE_SM), ReplaceSM.class);
        commands.put(Integer.valueOf(SMPPPacket.REPLACE_SM_RESP), ReplaceSMResp.class);
        commands.put(Integer.valueOf(SMPPPacket.SUBMIT_MULTI), SubmitMulti.class);
        commands.put(Integer.valueOf(SMPPPacket.SUBMIT_MULTI_RESP), SubmitMultiResp.class);
        commands.put(Integer.valueOf(SMPPPacket.SUBMIT_SM), SubmitSM.class);
        commands.put(Integer.valueOf(SMPPPacket.SUBMIT_SM_RESP), SubmitSMResp.class);
        commands.put(Integer.valueOf(SMPPPacket.UNBIND), Unbind.class);
        commands.put(Integer.valueOf(SMPPPacket.UNBIND_RESP), UnbindResp.class);
        this.commands = Collections.unmodifiableMap(commands);
    }

    /**
     * Create a new instance of the appropriate sub class of SMPPPacket. Packet
     * fields are all left at their default initial state.
     * 
     * @param id
     *            The SMPP command ID of the packet type to return.
     * @return A sub-class instance of {@link ie.omk.smpp.message.SMPPPacket}
     *         representing SMPP command <code>id</code>.
     * @throws ie.omk.smpp.BadCommandIDException
     *             if the command ID is not recognized.
     */
    public static SMPPPacket newInstance(int id) {
        return INSTANCE.newInstance(id, null);
    }
    
    /**
     * Get a response packet for the specified request. The returned response
     * packet will have its sequence number initialised to the same value
     * as <code>packet</code>.
     * @param packet The request packet to get a response for.
     * @return An SMPP response packet.
     * @throws BadCommandIDException If there is no response packet for the
     * specified request (for example, an <code>AlertNotification</code>).
     */
    public static SMPPPacket newResponse(SMPPPacket packet) {
        if (packet.isResponse()) {
            throw new IllegalArgumentException(
                    "Cannot create a response to a response!");
        }
        int id = packet.getCommandId();
        SMPPPacket response = INSTANCE.newInstance(id | 0x80000000, packet);
        response.setSequenceNum(packet.getSequenceNum());
        return response;
    }

    /**
     * Register a vendor packet with the factory. The SMPP allows for
     * vendor-specific packets to be defined. In order for these to be
     * usable with the API, primarily so that they can be identified and
     * decoded when received from an SMSC, they must be registered with
     * the packet factory.
     * <p>
     * This implementation assumes that the ID of the response packet will
     * be the ID of the request packet ORed with <code>0x80000000</code>.
     * This implementation also accepts <code>null</code> for the
     * <code>responseType</code> since there is at least one incidence in
     * the specification of such a case (<code>AlertNotification</code> has
     * no response packet).
     * </p>
     * @param id The command ID of the request packet.
     * @param requestType The class which implements the vendor request packet.
     * @param responseType The class which implements the vendor response
     * packet.
     */
    public static void registerVendorPacket(int id,
            Class<? extends SMPPPacket> requestType,
            Class<? extends SMPPPacket> responseType) {
        INSTANCE.userCommands.put(Integer.valueOf(id), requestType);
        if (responseType != null) {
            INSTANCE.userCommands.put(
                    Integer.valueOf(id | 0x80000000), responseType);
        }
    }

    public static void unregisterVendorPacket(int id) {
        INSTANCE.userCommands.remove(Integer.valueOf(id));
        INSTANCE.userCommands.remove(Integer.valueOf(id | 0x80000000));
    }
    
    /**
     * Get a new instance of an SMPP packet for the specified ID.
     * @param id The command ID to get the packet object for.
     * @param request If a response packet is being created, this parameter
     * may be optionally supplied and an attempt will be made to call a
     * constructor which accepts an SMPPPacket as its argument. All of the
     * response packets that are supplied as part of the API have such
     * a constructor.
     * @return A new instance of the relevant SMPPPacket implementation.
     * @throws BadCommandIDException If no matching class can be found for
     * <code>id</code>.
     */
    private SMPPPacket newInstance(int id, SMPPPacket request) {
        SMPPPacket response = null;
        Class<? extends SMPPPacket> clazz = getClassForId(id);
        if (clazz == null) {
            throw new BadCommandIDException(
                    "Unrecognized command id " + Integer.toHexString(id), id);
        }
        try {
            if (request != null) {
                response = constructWithPacketArg(clazz, request);
            }
            if (response == null) {
                response = clazz.newInstance();
            }
        } catch (Exception x) {
            throw new BadCommandIDException("Exception while calling constructor", x);
        }
        return response;
    }
    
    /**
     * Construct an SMPPPacket implementation class using a single-argument
     * constructor which takes an SMPPPacket object as its argument.
     * @param clazz The class to instantiate.
     * @param request The object to pass to the constructor.
     * @return The instantiated class, or <code>null</code> if the class does
     * not implement a single-argument constructor which accepts an SMPPPacket.
     * @throws Exception Any exception that is thrown by
     * {@link Constructor#newInstance(java.lang.Object[])} can be thrown
     * by this method.
     */
    private SMPPPacket constructWithPacketArg(
            Class<? extends SMPPPacket> clazz,
            SMPPPacket request) throws Exception {
        SMPPPacket packet = null;
        try {
            Constructor<? extends SMPPPacket> cons = clazz.getConstructor(
                    new Class[] {SMPPPacket.class});
            packet = cons.newInstance(
                    new Object[] {request});
        } catch (NoSuchMethodException x) {
            LOG.debug("No SMPPPacket constructor; will fall back to default.");
        }
        return packet;
    }
    
    /**
     * Get the implementation class for SMPP <code>commandId</code>.
     * The internally supplied SMPPPacket implementations will be queried
     * first, followed by all registered vendor packets.
     * @param commandId The command ID of the packet to get.
     * @return The implementing class, or <code>null</code> if there is
     * no class for the specified command ID.
     */
    private Class<? extends SMPPPacket> getClassForId(int commandId) {
        Integer id = Integer.valueOf(commandId);
        Class<? extends SMPPPacket> clazz = commands.get(id);
        if (clazz == null) {
            clazz = userCommands.get(id);
        }
        return clazz;
    }
}
