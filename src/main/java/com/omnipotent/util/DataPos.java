package com.omnipotent.util;

import com.github.bsideup.jabel.Desugar;

@Desugar
public record DataPos(double X, double Y, double Z) {
    @Override
    public String toString() {
        return "X: " + X + " Y: " + Y + " Z: " + Z;
    }
}
