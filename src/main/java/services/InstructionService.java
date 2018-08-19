package services;

import com.google.common.collect.TreeMultiset;
import domain.Instruction;
import domain.Type;
import output.ProcessedInstructions;
import utils.InstructionValidator;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Currency;
import java.util.List;
import java.util.Objects;
import java.util.SortedMap;
import java.util.logging.Logger;

public class InstructionService {

    private final Logger LOG = Logger.getLogger(InstructionService.class.getName());

    private final InstructionValidator instructionValidator = new InstructionValidator();

    /**
     * @param instructions list of incoming and outgoing instructions
     * @return object with instructions ordered by amount in USD and classified by settlement date
     */
    public ProcessedInstructions addInstructions(List<Instruction> instructions) {
        Objects.requireNonNull(instructions, "Please provide a valid list of instructions");
        ProcessedInstructions processedInstructions = new ProcessedInstructions();
        for (Instruction instruction : instructions) {
            if (instructionValidator.isValid(instruction)) {
                addInstruction(instruction, processedInstructions);
            } else {
                LOG.warning("Instruction " + instruction + " could not be added. Both conversion factor and price per unit should have valid values");
            }
        }
        return processedInstructions;
    }

    private void addInstruction(Instruction instruction, ProcessedInstructions processedInstructions) {
        adaptSettlementDates(instruction);
        if (instruction.getType() == Type.SELL) {
            saveIncomingInstruction(instruction, processedInstructions);
        } else {
            saveOutgoingInstruction(instruction, processedInstructions);
        }
    }

    private void saveIncomingInstruction(Instruction instruction, ProcessedInstructions processedInstructions) {
        saveInstruction(instruction, processedInstructions.getSortedIncomingInstructions(),
                processedInstructions.getUsdIncomingAmountPerDate());
    }

    private void saveOutgoingInstruction(Instruction instruction, ProcessedInstructions processedInstructions) {
        saveInstruction(instruction, processedInstructions.getSortedOutgoingInstructions(),
                processedInstructions.getUsdOutgoingAmountPerDate());
    }

    /**
     * @param instruction      instruction to be saved
     * @param instructions     sorted set by amount in USD
     * @param amountPerDateMap map which contains accumulated amount per date
     */
    private void saveInstruction(Instruction instruction, TreeMultiset<Instruction> instructions,
                                 SortedMap<LocalDate, BigDecimal> amountPerDateMap) {
        instructions.add(instruction);
        accumulateAmountToDate(instruction, amountPerDateMap);
    }

    /**
     * @param instruction      instruction with settlementDate
     * @param amountPerDateMap map which stores the total amount in USD per date
     */
    private void accumulateAmountToDate(Instruction instruction, SortedMap<LocalDate, BigDecimal> amountPerDateMap) {
        LocalDate settlementDate = instruction.getSettlementDate();
        BigDecimal amountToDate = amountPerDateMap.getOrDefault(settlementDate, BigDecimal.ZERO);
        amountPerDateMap.put(settlementDate, amountToDate.add(InstructionService.getAmountInUSD(instruction)));
    }

    /**
     * @param instruction Change the settlement date to the next working day if necessary
     */
    private void adaptSettlementDates(Instruction instruction) {
        if (isInDirhams(instruction) || isInRiyals(instruction)) {
            calculateSettlementDateForMiddleEastCurrency(instruction);
        } else {
            calculateSettlementDateForWesternCurrency(instruction);
        }
    }

    private void calculateSettlementDateForWesternCurrency(Instruction instruction) {
        calculateSettlementDate(instruction, DayOfWeek.MONDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
    }

    private void calculateSettlementDateForMiddleEastCurrency(Instruction instruction) {
        calculateSettlementDate(instruction, DayOfWeek.SUNDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY);
    }

    private void calculateSettlementDate(Instruction instruction, DayOfWeek firstWeekDay, DayOfWeek firstRestDay, DayOfWeek secondRestDay) {
        LocalDate settlementDate = instruction.getSettlementDate();
        if (settlementDate.getDayOfWeek() == firstRestDay || settlementDate.getDayOfWeek() == secondRestDay) {
            LocalDate nextWorkingDate = settlementDate.with(TemporalAdjusters.next(firstWeekDay));
            instruction.setSettlementDate(nextWorkingDate);
            LOG.info("The settlement date was changed because the instruction date falls on weekend");
        }
    }

    private boolean isInRiyals(Instruction instruction) {
        return matchesCurrency(instruction, "SAR");
    }

    private boolean isInDirhams(Instruction instruction) {
        return matchesCurrency(instruction, "AED");
    }

    private boolean matchesCurrency(Instruction instruction, String sar) {
        return instruction.getCurrency().equals(Currency.getInstance(sar));
    }

    public static BigDecimal getAmountInUSD(Instruction instruction) {
        return instruction.getPricePerUnit().multiply(instruction.getAgreedFx()).multiply(new BigDecimal(instruction.getUnits()));
    }
}
