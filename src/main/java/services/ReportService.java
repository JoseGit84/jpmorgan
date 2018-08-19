package services;

import com.google.common.collect.TreeMultiset;
import domain.Instruction;
import output.ProcessedInstructions;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ReportService {

    private static final String ROWS_FORMAT = "%10s%10s%10s%19s%18s%10s%16s%15s\n";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/uuuu");

    public static void printIncomingRanking(ProcessedInstructions processedInstructions) {
        System.out.println("\nINCOMING INSTRUCTIONS:\n");

        printRecords(processedInstructions.getSortedIncomingInstructions());
    }

    public static void printOutgoingRanking(ProcessedInstructions processedInstructions) {
        System.out.println("\nOUTGOING INSTRUCTIONS:\n");

        printRecords(processedInstructions.getSortedOutgoingInstructions());
    }

    private static void printRecords(TreeMultiset<Instruction> incomingInstructions) {
        System.out.format(ROWS_FORMAT, "Entity", "AgreedFx", "Currency",
                "Instruction Date", "Settlement Date", "Units", "Price per unit", "Amount in USD");
        for (Instruction instruction : incomingInstructions) {
            System.out.format(ROWS_FORMAT, instruction.getEntity(),
                    instruction.getAgreedFx().setScale(2, RoundingMode.HALF_UP), instruction.getCurrency(),
                    instruction.getInstructionDate(), instruction.getSettlementDate(), instruction.getUnits(), instruction.getPricePerUnit(),
                    InstructionService.getAmountInUSD(instruction).setScale(2, RoundingMode.HALF_UP));
        }
    }

    public static void printIncomingByDate(ProcessedInstructions processedInstructions, LocalDate date) {
        System.out.println(String.format("\nINCOMING INSTRUCTIONS ON %s:\n", date.format(DATE_FORMAT)));

        printByDate(processedInstructions.getSortedIncomingInstructions(), date);
    }

    public static void printOutgoingByDate(ProcessedInstructions processedInstructions, LocalDate date) {
        System.out.println(String.format("\nOUTGOING INSTRUCTIONS ON %s:\n", date.format(DATE_FORMAT)));

        printByDate(processedInstructions.getSortedOutgoingInstructions(), date);
    }

    private static void printByDate(TreeMultiset<Instruction> instructions, LocalDate date) {
        System.out.format(ROWS_FORMAT, "Entity", "AgreedFx", "Currency",
                "Instruction Date", "Settlement Date", "Units", "Price per unit", "Amount in USD");
        List<Instruction> filteredInstructions = instructions.stream().filter(i -> i.getSettlementDate().equals(date)).collect(Collectors.toList());
        for (Instruction instruction : filteredInstructions) {
            System.out.format(ROWS_FORMAT, instruction.getEntity(),
                    instruction.getAgreedFx().setScale(2, RoundingMode.HALF_UP), instruction.getCurrency(),
                    instruction.getInstructionDate(), instruction.getSettlementDate(), instruction.getUnits(), instruction.getPricePerUnit(),
                    InstructionService.getAmountInUSD(instruction).setScale(2, RoundingMode.HALF_UP));
        }
    }
}
