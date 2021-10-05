package com.palyrobotics.frc2020.util.TrajectoryReader;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class TrajectoryParser extends StdDeserializer<TrajectoryParser> {

    public TrajectoryParser() {
        this(null);
    }

    public TrajectoryParser(Class<?> vc) {
        super(vc);
    }
}