package output;

import com.google.common.collect.TreeMultiset;
import domain.Instruction;
import utils.ComparatorByUsdAmountDescending;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Processed instructions: with adapted settlement dates
 */
public class ProcessedInstructions {

    //Incoming and outgoing instructions ordered by amount in USD
    private final TreeMultiset<Instruction> sortedIncomingInstructions = TreeMultiset.create(new ComparatorByUsdAmountDescending());
    private final TreeMultiset<Instruction> sortedOutgoingInstructions = TreeMultiset.create(new ComparatorByUsdAmountDescending());

    //Incoming and outgoing instructions grouped by settlement date
    private final SortedMap<LocalDate, BigDecimal> usdOutgoingAmountPerDate = new TreeMap<>();
    private final SortedMap<LocalDate, BigDecimal> usdIncomingAmountPerDate = new TreeMap<>();

    public SortedMap<LocalDate, BigDecimal> getUsdIncomingAmountPerDate() {
        return usdIncomingAmountPerDate;
    }

    public SortedMap<LocalDate, BigDecimal> getUsdOutgoingAmountPerDate() {
        return usdOutgoingAmountPerDate;
    }

    public TreeMultiset<Instruction> getSortedIncomingInstructions() {
        return sortedIncomingInstructions;
    }

    public TreeMultiset<Instruction> getSortedOutgoingInstructions() {
        return sortedOutgoingInstructions;
    }
}
