/* ====================================================================
 * Limited Evaluation License:
 *
 * This software is open source, but licensed. The license with this package
 * is an evaluation license, which may not be used for productive systems. If
 * you want a full license, please contact us.
 *
 * The exclusive owner of this work is the OpenRate project.
 * This work, including all associated documents and components
 * is Copyright of the OpenRate project 2006-2014.
 *
 * The following restrictions apply unless they are expressly relaxed in a
 * contractual agreement between the license holder or one of its officially
 * assigned agents and you or your organisation:
 *
 * 1) This work may not be disclosed, either in full or in part, in any form
 *    electronic or physical, to any third party. This includes both in the
 *    form of source code and compiled modules.
 * 2) This work contains trade secrets in the form of architecture, algorithms
 *    methods and technologies. These trade secrets may not be disclosed to
 *    third parties in any form, either directly or in summary or paraphrased
 *    form, nor may these trade secrets be used to construct products of a
 *    similar or competing nature either by you or third parties.
 * 3) This work may not be included in full or in part in any application.
 * 4) You may not remove or alter any proprietary legends or notices contained
 *    in or on this work.
 * 5) This software may not be reverse-engineered or otherwise decompiled, if
 *    you received this work in a compiled form.
 * 6) This work is licensed, not sold. Possession of this software does not
 *    imply or grant any right to you.
 * 7) You agree to disclose any changes to this work to the copyright holder
 *    and that the copyright holder may include any such changes at its own
 *    discretion into the work
 * 8) You agree not to derive other works from the trade secrets in this work,
 *    and that any such derivation may make you liable to pay damages to the
 *    copyright holder
 * 9) You agree to use this software exclusively for evaluation purposes, and
 *    that you shall not use this software to derive commercial profit or
 *    support your business or personal activities.
 *
 * This software is provided "as is" and any expressed or impled warranties,
 * including, but not limited to, the impled warranties of merchantability
 * and fitness for a particular purpose are disclaimed. In no event shall
 * The OpenRate Project or its officially assigned agents be liable to any
 * direct, indirect, incidental, special, exemplary, or consequential damages
 * (including but not limited to, procurement of substitute goods or services;
 * Loss of use, data, or profits; or any business interruption) however caused
 * and on theory of liability, whether in contract, strict liability, or tort
 * (including negligence or otherwise) arising in any way out of the use of
 * this software, even if advised of the possibility of such damage.
 * This software contains portions by The Apache Software Foundation, Robert
 * Half International.
 * ====================================================================
 */
package WeblinksInterconnect;

import OpenRate.record.ChargePacket;
import OpenRate.record.ErrorType;
import OpenRate.record.RatingRecord;
import OpenRate.record.RecordError;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A Record corresponds to a unit of work that is being processed by the
 * pipeline. Records are created in the InputAdapter, pass through the Pipeline,
 * and written out in the OutputAdapter. Any stage of the pipeline my update the
 * record in any way, provided that later stages in the processing and the
 * output adapter know how to treat the record they receive.
 *
 * As an alternative, you may define a less flexible record format as you wish
 * and fill in the fields as required, but this costs performance.
 *
 * Generally, the record should know how to handle the following operations by
 * linking the appropriate method:
 *
 * mapOriginalData() [mandatory] ----------------- Transformation from a flat
 * record as read by the input adapter to a formatted record.
 *
 * unmapOriginalData() [mandatory if you wish to write output files]
 * ------------------- Transformation from a formatted record to a flat record
 * ready for output.
 *
 * getDumpInfo() [optional] ------------- Preparation of the dump equivalent of
 * the formatted record, ready for dumping out to a dump file.
 *
 * In this simple example, we require only to read the "B-Number", and write the
 * "Destination" as a result of this. Because of the simplicity of the example
 * we do not perform a full mapping, we just handle the fields we want directly,
 * which is one of the advantages of the BBPA model (map as much as you want or
 * as little as you have to).
 *
 */
public class WIRecord extends RatingRecord {

  // Events

  public final static int WI_RECORD_TYPE = 20;
  private final static int FIELD_COUNT = 26;
  private final static int IDX_KEY_SYS_ID = 0;
  private final static int IDX_INSTANCE = 1;
  private final static int IDX_RECORD_TYPE = 2;
  private final static int IDX_SERVICE_CENTRE = 3;
  private final static int IDX_SERVED_IMSI = 4;
  private final static int IDX_SERVED_IMEI = 5;
  private final static int IDX_SERVED_MSISDN = 6;
  private final static int IDX_CALLING_NUMBER = 7;
  private final static int IDX_CALLED_NUMBER = 8;
  private final static int IDX_RECORDING_ENTITY = 9;
  private final static int IDX_INCOMING_TRUNK = 10;
  private final static int IDX_OUTGOING_TRUNK = 11;
  private final static int IDX_LOCATION_MSC_NUMBER = 12;
  private final static int IDX_SUPS_USED = 13;
  private final static int IDX_MS_CLASSMARK = 14;
  private final static int IDX_SEIZURE_TIME = 15;
  private final static int IDX_ANSWER_TIME = 16;
  private final static int IDX_RELEASE_TIME = 17;
  private final static int IDX_CALL_DURATION = 18;
  private final static int IDX_CAUSE_FOR_TERM = 19;
  private final static int IDX_DIAGNOSTICS = 20;
  private final static int IDX_CALL_REFERENCE = 21;
  private final static int IDX_SEQUENCE_NUMBER = 22;
  private final static int IDX_RECORD_EXTENSIONS = 23;
  private final static int IDX_UNKNOWN_FIELD = 24;
  private final static int IDX_SRC_CODE = 25;

  private static final long serialVersionUID = 1126384623L;

  // Worker variables to save references during processing.
  public int MSC_Record_Type;
  public String CallCase = null;
  public String Direction;
  public int tmpCauseForTerm;
  public String CauseForTerm;

  // These variables are filled according to the traffic case, and are therefore
  // to be treated as normalised
  public String Other_Party_Number = null;  // Normalised other party number
  public String DestDescription = null;     // Description of the zone
  public String TrunkIn;                    // In trunk ID
  public String TrunkOut;                   // Out trunk ID
  public String TrunkInName;                // In trunk readable name
  public String TrunkOutName;               // Out trunk readable name

  // These are the rating fields
  public double Duration = 0;
  public String IMSI = null;
  public double RatedAmount = 0;
  public String ZoneModel = "";
  public String ZoneResult;
  public String PriceModel = null;
  public ChargePacket tmpCP;
  public String PartnerOperator;
  public String CDRMonth;

  // Used for duplicate check
  public String DupChkKey;

  // This is used to interpret the dates in the input
  private final SimpleDateFormat sdfIn;
  private final SimpleDateFormat sdfCDRMonth;
  public String ContractZoneResult;
  public String ContractDestDescription;
  public ArrayList<String> AggFilter;

  /**
   * Default Constructor for RateRecord, creating the empty record container
   */
  public WIRecord() {
    super();
    sdfIn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    sdfIn.setLenient(false);
    sdfCDRMonth = new SimpleDateFormat("yyyyMM");
  }

  /**
   * Splits the original record into fields in the internal array
   *
   * @param newOriginalData The data to be loaded into the internal field array
   */
  public void loadFields(String newOriginalData) {
    OriginalData = newOriginalData;
    fields = OriginalData.split(";");
  }

  /**
   * Utility function to map a main record
   *
   * @return
   */
  public boolean mapRecord() {
    RecordError tmpError;
    String DateToUse;

    // For this application, it is enough to know that the detail is "020"
    RECORD_TYPE = WI_RECORD_TYPE;

    // split the record based on tabs
    fields = OriginalData.split("\t");

    if (fields.length == FIELD_COUNT) {
      // Map the call case
      // Parse the duration
      try {
        MSC_Record_Type = Integer.parseInt(getField(IDX_RECORD_TYPE));
      } catch (NumberFormatException nfe) {
        Duration = 0;
        tmpError = new RecordError("ERR_RECORD_TYPE_INVALID", ErrorType.DATA_VALIDATION);
        addError(tmpError);
      }

      // provide a readable type
      switch (MSC_Record_Type) {
        case 0: {
          CallCase = "MOC";
          Direction = "Originating";
          Other_Party_Number = getField(IDX_CALLED_NUMBER);
          Service = "Voice";
        }
        break;
        case 1: {
          CallCase = "MTC";
          Direction = "Terminating";

            // Invert the called and calling so that in the future we can
          // identify the other party using zoning (Currently not used)
          Other_Party_Number = getField(IDX_CALLING_NUMBER);
          Service = "Voice";
        }
        break;
        case 2: {
          CallCase = "ROAMING";
          Direction = "Roaming";
          Other_Party_Number = "Unknown";
          Service = "Voice";
        }
        break;
        case 3: {
          CallCase = "POC";
          Direction = "Terminating";

            // Invert the called and calling so that in the future we can
          // identify the other party using zoning (Currently not used)
          Other_Party_Number = getField(IDX_CALLING_NUMBER);
          Service = "Voice";
        }
        break;
        case 4: {
          CallCase = "PTC";
          Direction = "Originating";
          Other_Party_Number = getField(IDX_CALLED_NUMBER);
          Service = "Voice";
        }
        break;
        case 5: {
          CallCase = "TRANSIT";
          Direction = "Transit";
          Other_Party_Number = "Unknown";
          Service = "Voice";
        }
        break;
        case 6: {
          CallCase = "SMMO";
          Direction = "Originating";
          Other_Party_Number = getField(IDX_CALLED_NUMBER);
          Service = "SMS";
        }
        break;
        case 7: {
          CallCase = "SMMT";
          Direction = "Terminating";

            // Invert the called and calling so that in the future we can
          // identify the other party using zoning (Currently not used)
          Other_Party_Number = getField(IDX_CALLING_NUMBER);
          Service = "SMS";
        }
        break;
        default: {
          CallCase = "Unknown";
          Direction = "Unknown";
          Other_Party_Number = "Unknown";
          Service = "Unknown";

          // Error the record to avoid further processing
          tmpError = new RecordError("ERR_UNKNOWN_CALL_CASE", ErrorType.DATA_VALIDATION);
          addError(tmpError);
        }
        break;
      }

      // Map the voice only things
      if (Service.equals("Voice")) {
        // Map the reason for closure
        // Parse the duration
        try {
          tmpCauseForTerm = Integer.parseInt(getField(IDX_CAUSE_FOR_TERM));
        } catch (NumberFormatException nfe) {
          Duration = 0;
          tmpError = new RecordError("ERR_CAUSE_FOR_TERM_INVALID", ErrorType.DATA_VALIDATION);
          addError(tmpError);
        }

        // provide a readable type
        switch (tmpCauseForTerm) {
          case 0: {
            CauseForTerm = "Caller Clear";
          }
          break;
          case 2: {
            CauseForTerm = "No Answer";
            tmpError = new RecordError("ERR_CAUSE_FOR_TERM_NO_CALL", ErrorType.DATA_VALIDATION);
            addError(tmpError);
          }
          break;
          case 3: {
            CauseForTerm = "Unsuccessful Call Attempt";
            tmpError = new RecordError("ERR_CAUSE_FOR_TERM_NO_CALL", ErrorType.DATA_VALIDATION);
            addError(tmpError);
          }
          break;
          case 4: {
            CauseForTerm = "Subscriber Busy";
            tmpError = new RecordError("ERR_CAUSE_FOR_TERM_NO_CALL", ErrorType.DATA_VALIDATION);
            addError(tmpError);
          }
          break;
          case 6: {
            CauseForTerm = "Trunk Busy";
            tmpError = new RecordError("ERR_CAUSE_FOR_TERM_NO_CALL", ErrorType.DATA_VALIDATION);
            addError(tmpError);
          }
          break;
          case 8: {
            CauseForTerm = "Call Denied";
            tmpError = new RecordError("ERR_CAUSE_FOR_TERM_NO_CALL", ErrorType.DATA_VALIDATION);
            addError(tmpError);
          }
          break;
          case 10: {
            CauseForTerm = "Signalling Error";
            tmpError = new RecordError("ERR_CAUSE_FOR_TERM_NO_CALL", ErrorType.DATA_VALIDATION);
            addError(tmpError);
          }
          break;
          case 16: {
            CauseForTerm = "Normal Clear";
          }
          break;
          default: {
            CauseForTerm = "Unknown";
            tmpError = new RecordError("ERR_UNKNOWN_CAUSE_FOR_TERM", ErrorType.DATA_VALIDATION);
            addError(tmpError);
          }
          break;
        }

        // Parse the duration
        try {
          Duration = Double.parseDouble(getField(IDX_CALL_DURATION));
        } catch (NumberFormatException nfe) {
          Duration = 0;
          tmpError = new RecordError("ERR_DURATION_INVALID", ErrorType.DATA_VALIDATION);
          addError(tmpError);
        }
      }

      // Map the trunks
      TrunkIn = getField(IDX_INCOMING_TRUNK);
      TrunkOut = getField(IDX_OUTGOING_TRUNK);

      // Get the CDR date
      try {
        // Pre load the variables with something that will allow processing
        // for the case that we can't get the date
        EventStartDate = sdfIn.parse("2008-01-01 00:00:00");
        CDRMonth = "Unknown";

        // Get the call start time
        if (getField(IDX_ANSWER_TIME).length() > 6) {
          // Seems we have a valid seizure time, so use it
          DateToUse = getField(IDX_ANSWER_TIME);
        } else if (getField(IDX_SEIZURE_TIME).length() > 6) {
          // Seems we have a valid answer time, so use it
          DateToUse = getField(IDX_SEIZURE_TIME);
        } else {
          // Well, neither of those worked, so how about this one?
          DateToUse = getField(IDX_RELEASE_TIME);
        }

        // Map the CDR Month
        EventStartDate = sdfIn.parse(DateToUse);

        // Get the UTC version of the date
        UTCEventDate = EventStartDate.getTime() / 1000;

        // Get the month of the CDR for aggregation
        CDRMonth = sdfCDRMonth.format(EventStartDate);
      } catch (ParseException ex) {
        tmpError = new RecordError("ERR_DATE_INVALID", ErrorType.DATA_VALIDATION);
        addError(tmpError);
      }

      // Get the served IMSI
      IMSI = getField(IDX_SERVED_IMSI);

      if (IMSI.equals("\\N")) {
        IMSI = "";
      }

      // Map the duplicate Check Key
      DupChkKey = EventStartDate.getTime() + CallCase + getField(WIRecord.IDX_CALL_REFERENCE);

      // get the RUM values
      setRUMValue("DUR", Duration);
      setRUMValue("EVT", 1);
    } else {
      // This is not a valid record - it has the wrong number of fields
      tmpError = new RecordError("ERR_INVALID_RECORD_FORMAT", ErrorType.DATA_VALIDATION);
      addError(tmpError);
      return false;
    }

    return true;
  } //--- end mapRecord

  /**
   * Reconstruct the record from the field values, replacing the original
   * structure of tab separated records
   *
   * @return The unmapped internal field data
   */
  public String unmapOriginalData() {

    int NumberOfFields;
    int i;
    StringBuilder tmpReassemble;

    if (RECORD_TYPE == WIRecord.WI_RECORD_TYPE) {
      // We use the string buffer for the reassembly of the record. Avoid
      // just catenating strings, as it is a LOT slower because of the
      // java internal string handling (it has to allocate/deallocate many
      // times to rebuild the string).
      tmpReassemble = new StringBuilder(1024);

      // Update charge value
      //setField(FIELD_E_CHARGE, Double.toString(RatedAmount));
      NumberOfFields = fields.length;

      for (i = 0; i < NumberOfFields; i++) {

        if (i == 0) {
          tmpReassemble.append(fields[i]);
        } else {
          tmpReassemble.append(";");
          tmpReassemble.append(fields[i]);
        }
      }

      // Add subscription id
      tmpReassemble.append(";");

      // return the re-assembled string
      return tmpReassemble.toString();
    }

    return OriginalData;
  }

  /**
   * Reconstruct the record from the field values, replacing the original
   * structure of tab separated records
   *
   * @return The unmapped internal field data
   */
  public String unmapRatedData() {

    int NumberOfFields;
    int i;
    StringBuilder tmpReassemble;

    if (RECORD_TYPE == WIRecord.WI_RECORD_TYPE) {
      // We use the string buffer for the reassembly of the record. Avoid
      // just catenating strings, as it is a LOT slower because of the
      // java internal string handling (it has to allocate/deallocate many
      // times to rebuild the string).
      tmpReassemble = new StringBuilder(1024);

      // Put the Rated Fields
      tmpReassemble.append(CallCase);
      tmpReassemble.append(";");
      tmpReassemble.append(PartnerOperator);
      tmpReassemble.append(";");
      tmpReassemble.append(CDRMonth);
      tmpReassemble.append(";");
      tmpReassemble.append(Duration);
      tmpReassemble.append(";");
      tmpReassemble.append(RatedAmount);
      tmpReassemble.append(";");
      tmpReassemble.append(Other_Party_Number);
      tmpReassemble.append(";");
      tmpReassemble.append(ZoneModel);
      tmpReassemble.append(";");
      tmpReassemble.append(DestDescription);
      tmpReassemble.append(";");

      // Put the original fields
      NumberOfFields = fields.length;

      for (i = 0; i < NumberOfFields; i++) {

        if (i == 0) {
          tmpReassemble.append(fields[i]);
        } else {
          tmpReassemble.append(";");
          tmpReassemble.append(fields[i]);
        }
      }

      // Add final terminator
      tmpReassemble.append(";");

      // return the re-assembled string
      return tmpReassemble.toString();
    }

    return OriginalData;
  }

  /**
   * Reconstruct the record from the field values, replacing the original
   * structure of tab separated records
   *
   * @return The unmapped internal field data
   */
  public String unmapErrorData() {

    int NumberOfFields;
    int i;
    StringBuilder tmpReassemble;

    if (RECORD_TYPE == WIRecord.WI_RECORD_TYPE) {
      // We use the string buffer for the reassembly of the record. Avoid
      // just catenating strings, as it is a LOT slower because of the
      // java internal string handling (it has to allocate/deallocate many
      // times to rebuild the string).
      tmpReassemble = new StringBuilder(1024);

      // Put the Rated Fields
      tmpReassemble.append(ZoneModel);
      tmpReassemble.append(";");
      tmpReassemble.append(getErrors().get(0).getMessage());
      tmpReassemble.append(";");

      // Put the original fields
      NumberOfFields = fields.length;

      for (i = 0; i < NumberOfFields; i++) {

        if (i == 0) {
          tmpReassemble.append(fields[i]);
        } else {
          tmpReassemble.append(";");
          tmpReassemble.append(fields[i]);
        }
      }

      // Add final terminator
      tmpReassemble.append(";");

      // return the re-assembled string
      return tmpReassemble.toString();
    }

    return OriginalData;
  }

  /**
   * Return the dump-ready data
   *
   * @return The built information for dumping
   */
  @Override
  public ArrayList<String> getDumpInfo() {
    ArrayList<String> tmpDumpList;
    tmpDumpList = new ArrayList<>();
    Iterator<String> tmpOutputListIterator;

    if (RECORD_TYPE == WIRecord.WI_RECORD_TYPE) {
      // Format the fields
      tmpDumpList.add("============ DETAIL RECORD ============");
      tmpDumpList.add("  Outputs             = <" + getOutputs() + ">");
      tmpDumpList.add("  KEY_SYS_ID          = <" + getField(IDX_KEY_SYS_ID) + ">");
      tmpDumpList.add("  INSTANCE            = <" + getField(IDX_INSTANCE) + ">");
      tmpDumpList.add("  RECORD_TYPE         = <" + getField(IDX_RECORD_TYPE) + ">");
      tmpDumpList.add("  SERVICE_CENTRE      = <" + getField(IDX_SERVICE_CENTRE) + ">");
      tmpDumpList.add("  SERVED_IMSI         = <" + getField(IDX_SERVED_IMSI) + ">");
      tmpDumpList.add("  SERVED_IMEI         = <" + getField(IDX_SERVED_IMEI) + ">");
      tmpDumpList.add("  SERVED_MSISDN       = <" + getField(IDX_SERVED_MSISDN) + ">");
      tmpDumpList.add("  CALLING_NUMBER      = <" + getField(IDX_CALLING_NUMBER) + ">");
      tmpDumpList.add("  CALLED_NUMBER       = <" + getField(IDX_CALLED_NUMBER) + ">");
      tmpDumpList.add("  RECORDING_ENTITY    = <" + getField(IDX_RECORDING_ENTITY) + ">");
      tmpDumpList.add("  INCOMING_TRUNK      = <" + getField(IDX_INCOMING_TRUNK) + ">");
      tmpDumpList.add("  OUTGOING_TRUNK      = <" + getField(IDX_OUTGOING_TRUNK) + ">");
      tmpDumpList.add("  LOCATION_MSC_NUMBER = <" + getField(IDX_LOCATION_MSC_NUMBER) + ">");
      tmpDumpList.add("  SUPS_USED           = <" + getField(IDX_SUPS_USED) + ">");
      tmpDumpList.add("  MS_CLASSMARK        = <" + getField(IDX_MS_CLASSMARK) + ">");
      tmpDumpList.add("  SEIZURE_TIME        = <" + getField(IDX_SEIZURE_TIME) + ">");
      tmpDumpList.add("  ANSWER_TIME         = <" + getField(IDX_ANSWER_TIME) + ">");
      tmpDumpList.add("  RELEASE_TIME        = <" + getField(IDX_RELEASE_TIME) + ">");
      tmpDumpList.add("  CALL_DURATION       = <" + getField(IDX_CALL_DURATION) + ">");
      tmpDumpList.add("  CAUSE_FOR_TERM      = <" + getField(IDX_CAUSE_FOR_TERM) + ">");
      tmpDumpList.add("  DIAGNOSTICS         = <" + getField(IDX_DIAGNOSTICS) + ">");
      tmpDumpList.add("  CALL_REFERNCE       = <" + getField(IDX_CALL_REFERENCE) + ">");
      tmpDumpList.add("  SEQUENCE_NUMBER     = <" + getField(IDX_SEQUENCE_NUMBER) + ">");
      tmpDumpList.add("  RECORD_EXTENSIONS   = <" + getField(IDX_RECORD_EXTENSIONS) + ">");
      tmpDumpList.add("  SRC_CODE            = <" + getField(IDX_SRC_CODE) + ">");
      tmpDumpList.add("----------- Calculated Info ----------");
      tmpDumpList.add("  Call Case           = <" + CallCase + ">");
      tmpDumpList.add("  Direction           = <" + Direction + ">");
      tmpDumpList.add("  Destination         = <" + Other_Party_Number + ">");
      tmpDumpList.add("  Dest Description    = <" + DestDescription + ">");
      tmpDumpList.add("  Cause For Term      = <" + CauseForTerm + ">");
      tmpDumpList.add("  Trunk In            = <" + TrunkIn + ">");
      tmpDumpList.add("  Trunk In Name       = <" + TrunkInName + ">");
      tmpDumpList.add("  Trunk Out           = <" + TrunkOut + ">");
      tmpDumpList.add("  Trunk Out Name      = <" + TrunkOutName + ">");
      tmpDumpList.add("  Trunk Operator      = <" + PartnerOperator + ">");
      tmpDumpList.add("  CDR Month           = <" + CDRMonth + ">");
      tmpDumpList.add("------------- Rating Info ------------");
      tmpDumpList.add("  Zone Model          = <" + ZoneModel + ">");
      tmpDumpList.add("  B-Number            = <" + Other_Party_Number + ">");
      tmpDumpList.add("  Zone Result         = <" + ZoneResult + ">");
      tmpDumpList.add("  Duration            = <" + Duration + ">");
      tmpDumpList.add("  Rated Amount        = <" + RatedAmount + ">");
      tmpDumpList.add("  Contract Zone       = <" + ContractZoneResult + ">");
      tmpDumpList.add("  Contract Zone Desc  = <" + ContractDestDescription + ">");
      tmpDumpList.add("------------ Internal Info -----------");
      tmpDumpList.add("  Duplicate Key       = <" + DupChkKey + ">");
      tmpOutputListIterator = getOutputs().iterator();
      while (tmpOutputListIterator.hasNext()) {
        tmpDumpList.add("  Output Stream       = <" + tmpOutputListIterator.next() + ">");
      }

      // Add Charge Packets
      tmpDumpList.addAll(getChargePacketsDump());

      // Add Balance Impacts
      tmpDumpList.addAll(getBalanceImpactsDump());
    }

    // Add Errors
    tmpDumpList.addAll(getErrorDump());

    return tmpDumpList;
  }
}
