package com.jakevin.pokeshout.clipboard.tokens;

import android.content.Context;

import com.jakevin.pokeshout.clipboard.ClipboardToken;
import com.jakevin.pokeshout.logic.IVCombination;
import com.jakevin.pokeshout.logic.IVScanResult;
import com.jakevin.pokeshout.logic.PokeInfoCalculator;

/**
 * Created by Johan on 2016-09-26.
 * Get the pokemon iv as a hex representation.
 */

public class HexIVToken extends ClipboardToken {

    String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};

    public HexIVToken() {
        super(false);
    }

    @Override
    public int getMaxLength() {
        return 3;
    }

    @Override
    public String getValue(IVScanResult ivScanResult, PokeInfoCalculator pokeInfoCalculator) {
        IVCombination lowestIVCombination = ivScanResult.getLowestIVCombination();
        if (lowestIVCombination == null) {
            return "";
        }
        int att = lowestIVCombination.att;
        int def = lowestIVCombination.def;
        int sta = lowestIVCombination.sta;

        return hex[att] + hex[def] + hex[sta];
    }

    @Override
    public String getPreview() {
        return "9A3";
    }

    @Override
    public String getTokenName(Context context) {
        return "HexIV";
    }

    @Override
    public String getLongDescription(Context context) {
        return "Represents the Pokémon IVs in a base 16 number system. This means that each IV can be represented by "
                + "a single character. So for example, a pokemon with IVs 10, 13, 3 would be represented as AD3.";
    }

    @Override
    public String getCategory() {
        return "IV Info";
    }

    @Override
    public boolean changesOnEvolutionMax() {
        return false;
    }
}
