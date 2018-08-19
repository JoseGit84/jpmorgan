package utils;

import domain.Instruction;
import services.InstructionService;

import java.math.BigDecimal;
import java.util.Comparator;

public class ComparatorByUsdAmountDescending implements Comparator<Instruction> {
    @Override
    public int compare(Instruction instruction1, Instruction instruction2) {
        if (instruction1 == null) {
            return -1;
        }
        if (instruction2 == null) {
            return 1;
        }
        BigDecimal usdAmount1 = InstructionService.getAmountInUSD(instruction1);
        BigDecimal usdAmount2 = InstructionService.getAmountInUSD(instruction2);
        // Change sign to sort descendingly
        return -usdAmount1.compareTo(usdAmount2);
    }
}
