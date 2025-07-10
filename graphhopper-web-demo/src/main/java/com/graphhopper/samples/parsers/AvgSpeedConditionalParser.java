package com.graphhopper.samples.parsers;

import com.graphhopper.reader.ReaderWay;
import com.graphhopper.routing.ev.DecimalEncodedValue;
import com.graphhopper.routing.ev.EdgeIntAccess;
import com.graphhopper.routing.util.parsers.TagParser;
import com.graphhopper.storage.IntsRef;

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

        Pattern entryPattern = Pattern.compile("([\\d.]+)\\s*@\\s*\\(([^)]+)\\)");
        Matcher matcher = entryPattern.matcher(tag);

        while (matcher.find()) {
            double speed = Double.parseDouble(matcher.group(1));
            String condition = matcher.group(2).toLowerCase().replaceAll("[^a-z0-9:_]", "_").replace(":", "");

            String conditionKey = reverse ? "avgspeed_" + condition + "_backward"
                    : "avgspeed_" + condition + "_forward";

            if (conditionKey.equals(encodedValueKey + "_forward") && !reverse) {
                avgSpeedEnc.setDecimal(false, edgeId, edgeIntAccess, speed);
            } else if (conditionKey.equals(encodedValueKey + "_backward") && reverse) {
                avgSpeedEnc.setDecimal(true, edgeId, edgeIntAccess, speed);
            }
        }
        return;
    }
}