/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analysis;

import net.seninp.jmotif.sax.SAXException;
import net.seninp.jmotif.sax.SAXProcessor;
import net.seninp.jmotif.sax.alphabet.Alphabet;
import net.seninp.jmotif.sax.datastructure.SAXRecords;

/**
 *
 * @author Roberta
 */
public class SAXBuilder {

    private final int alphabetSize;
    private final double nThreshold;
    private final Alphabet na;

    public SAXBuilder(int alphabetSize, double nThreshold, Alphabet na) {
        this.alphabetSize = alphabetSize;
        this.nThreshold = nThreshold;
        this.na = na;
    }

    // from a timeseries of the word frequencies, create a SAX string that represents it
    public String buildSAX(double[] timeSeries) throws SAXException {
        SAXProcessor sp = new SAXProcessor();
        SAXRecords res = sp.ts2saxByChunking(timeSeries, timeSeries.length, na.getCuts(alphabetSize), nThreshold);
        return res.getSAXString("");
    }
}

