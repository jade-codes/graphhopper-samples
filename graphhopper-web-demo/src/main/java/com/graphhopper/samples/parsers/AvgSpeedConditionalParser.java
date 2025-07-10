package com.graphhopper.samples.parsers;

import com.graphhopper.reader.ReaderWay;
import com.graphhopper.routing.ev.DecimalEncodedValue;
import com.graphhopper.routing.ev.EdgeIntAccess;
import com.graphhopper.routing.util.parsers.TagParser;
import com.graphhopper.storage.IntsRef;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.*;

public class AvgSpeedConditionalParser implements TagParser {
    private final String encodedValueKey;
    private final DecimalEncodedValue avgSpeedEnc;

    public AvgSpeedConditionalParser(String encodedValueKey, DecimalEncodedValue avgSpeedEnc) {
        this.encodedValueKey = encodedValueKey;
        this.avgSpeedEnc = avgSpeedEnc;
    }

    @Override
    public void handleWayTags(int edgeId, EdgeIntAccess edgeIntAccess, ReaderWay way, IntsRef relationFlags) {
        processConditionalTag(edgeId, edgeIntAccess, way, true, relationFlags);
        processConditionalTag(edgeId, edgeIntAccess, way, false, relationFlags);
    }

    private void processConditionalTag(int edgeId, EdgeIntAccess edgeIntAccess, ReaderWay way, Boolean reverse,
            IntsRef relationFlags) {
        String tagKey = reverse ? "avgspeed:backward:conditional" : "avgspeed:forward:conditional";
        String tag = way.getTag(tagKey);
        if (tag == null)
            return;

        String currentCondition = conditionMap.get(encodedValueKey);
        Pattern pattern = Pattern.compile("([\\d.]+)\\s*@\\s*\\((" + Pattern.quote(currentCondition) + ")\\)");
        Matcher matcher = pattern.matcher(tag);

        if (matcher.find()) {
            double speed = Double.parseDouble(matcher.group(1));
            avgSpeedEnc.setDecimal(reverse, edgeId, edgeIntAccess, speed);
        }
        return;
    }
    
    private final HashMap<String, String> conditionMap = new HashMap<>() {
        {
            put("avgspeed_mo_fr_0400_0700", "Mo-Fr 04:00-07:00");
            put("avgspeed_mo_fr_0700_0900", "Mo-Fr 07:00-09:00");
            put("avgspeed_mo_fr_0900_1200", "Mo-Fr 09:00-12:00");
            put("avgspeed_mo_fr_1200_1400", "Mo-Fr 12:00-14:00");
            put("avgspeed_mo_fr_1400_1600", "Mo-Fr 14:00-16:00");
            put("avgspeed_mo_fr_1600_1900", "Mo-Fr 16:00-19:00");
            put("avgspeed_mo_fr_1900_2200", "Mo-Fr 19:00-22:00");
            put("avgspeed_mo_fr_2200_0400", "Mo-Fr 22:00-04:00");
            put("avgspeed_sa_su_0400_0700", "Sa-Su 04:00-07:00");
            put("avgspeed_sa_su_0700_1000", "Sa-Su 07:00-10:00");
            put("avgspeed_sa_su_1000_1400", "Sa-Su 10:00-14:00");
            put("avgspeed_sa_su_1400_1900", "Sa-Su 14:00-19:00");
            put("avgspeed_sa_su_1900_2200", "Sa-Su 19:00-22:00");
            put("avgspeed_sa_su_2200_0400", "Sa-Su 22:00-04:00");
        }
    };
}