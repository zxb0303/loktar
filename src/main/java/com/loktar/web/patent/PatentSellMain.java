package com.loktar.web.patent;

import com.loktar.dto.patent.PatentQuotationDTO;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.Units;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class PatentSellMain {
    //TODO start 公司名称和营业执照格式 每个公司需要确认后修改
    public static String COMPANT_NAME = "苏州市益鑫源科技有限公司";
    public static String BUSINESS_LICENSE_NAME = "营业执照.jpg";
    //TODO end
    public static String BASE_FOLD_PATH = "F:/OneDrive/Patent/成交资料/";
    public static String JP_FILE_NAME = "解聘书.docx";
    public static String BG_FILE_NAME = "变更协议.docx";
    public static String DL_FILE_NAME = "专利代理委托书.docx";
    public static String SEAL_FILE_NAME = "公章.png";
    public static String TEMPLATE_FOLD_PATH = BASE_FOLD_PATH + "/模版/";
    public static String JP_TEMPLATE_FILE_PATH = TEMPLATE_FOLD_PATH + JP_FILE_NAME;
    public static String BG_TEMPLATE_FILE_PATH = TEMPLATE_FOLD_PATH + BG_FILE_NAME;
    public static String DL_TEMPLATE_PATH = TEMPLATE_FOLD_PATH + DL_FILE_NAME;
    public static String COMPANT_FOLD_PATH = BASE_FOLD_PATH + COMPANT_NAME;
    public static String QUOTATION_FILE_PATH = COMPANT_FOLD_PATH + "/" + COMPANT_NAME + ".xlsx";
    public static String SELL_FOLD_NAME = COMPANT_NAME + "-出售资料";
    public static String SELL_FOLD_PATH = BASE_FOLD_PATH + COMPANT_NAME + "/" + SELL_FOLD_NAME;
    public static String SEAL_PATH = COMPANT_FOLD_PATH + "/" + SEAL_FILE_NAME;
    public static String BUSINESS_LICENSE_PATH = COMPANT_FOLD_PATH + "/" + BUSINESS_LICENSE_NAME;

    @SneakyThrows
    public static void main(String[] args) {
        //创建出售文件夹
        createFolder(COMPANT_FOLD_PATH, SELL_FOLD_NAME);
        //复制模版中的解聘书、专利代理委托书、变更协议,并添加公章
        copyFile(JP_TEMPLATE_FILE_PATH, SELL_FOLD_PATH);
        addPicToFile(JP_FILE_NAME, 9);
        copyFile(BG_TEMPLATE_FILE_PATH, SELL_FOLD_PATH);
        addPicToFile(BG_FILE_NAME, 9);
        copyFile(DL_TEMPLATE_PATH, SELL_FOLD_PATH);
        addPicToFileHasTable(DL_FILE_NAME, 23);
        //读取报价单
        List<PatentQuotationDTO> patentQuotationDTOs = readQuotationExcel(QUOTATION_FILE_PATH);
        for (PatentQuotationDTO patentQuotationDTO : patentQuotationDTOs) {
            //创建单个专利文件夹
            String sellpatentPath = createFolder(SELL_FOLD_PATH, patentQuotationDTO.getPatentId() + patentQuotationDTO.getName());
            //复制证书
            copyFile(COMPANT_FOLD_PATH + "/" + patentQuotationDTO.getPatentId() + ".pdf", sellpatentPath);
            //复制公章
            copyFile(SEAL_PATH, sellpatentPath);
            //复制营业执照
            copyFile(BUSINESS_LICENSE_PATH, sellpatentPath);
            //复制解聘书、专利代理委托书、变更协议
            copyFile(SELL_FOLD_PATH + "/" + JP_FILE_NAME, sellpatentPath);
            copyFile(SELL_FOLD_PATH + "/" + BG_FILE_NAME, sellpatentPath);
            copyFile(SELL_FOLD_PATH + "/" + DL_FILE_NAME, sellpatentPath);
        }
        //复制报价单
        copyFile(QUOTATION_FILE_PATH, SELL_FOLD_PATH);
        deleteFile(SELL_FOLD_PATH + "/" + JP_FILE_NAME);
        deleteFile(SELL_FOLD_PATH + "/" + BG_FILE_NAME);
        deleteFile(SELL_FOLD_PATH + "/" + DL_FILE_NAME);
        //打包文件
//        zipSellFold(SELL_FOLD_PATH, BASE_FOLD_PATH + SELL_FOLD_NAME + ".zip");
        zipSellFold(SELL_FOLD_PATH, SELL_FOLD_PATH + ".zip");
        //删除文件夹
        deleteFolder(COMPANT_FOLD_PATH, SELL_FOLD_NAME);
        System.out.println("[" + COMPANT_NAME + "]打包完成");
    }

    @SneakyThrows
    private static void zipSellFold(String sourceDirPath, String zipFilePath) {
        Path sourceDir = Paths.get(sourceDirPath);
        Path zipPath = Paths.get(zipFilePath);
        if (Files.exists(zipPath)) {
            Files.delete(zipPath);
        }
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFilePath))) {
            Files.walk(sourceDir)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        String zipEntryName = sourceDir.relativize(path).toString();
                        try {
                            zos.putNextEntry(new ZipEntry(zipEntryName));
                            Files.copy(path, zos);
                            zos.closeEntry();
                        } catch (Exception e) {
                            System.err.println("压缩文件失败: " + path + " - " + e.getMessage());
                        }
                    });
        }
    }

    @SneakyThrows
    private static void deleteFile(String filepath) {
        Path path = Paths.get(filepath);
        Files.delete(path);
    }

    @SneakyThrows
    private static void addPicAfterTextInTable(String fileName, String searchText) {
        String sealFile = SEAL_PATH;
        String docPath = SELL_FOLD_PATH + "/" + fileName;
        XWPFDocument document = new XWPFDocument(new FileInputStream(docPath));
        FileOutputStream out = new FileOutputStream(docPath);
        FileInputStream is = new FileInputStream(sealFile);

        boolean pictureInserted = false;

        for (XWPFTable table : document.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        List<XWPFRun> runs = paragraph.getRuns();
                        for (int i = 0; i < runs.size(); i++) {
                            XWPFRun run = runs.get(i);
                            String text = run.getText(0);
                            if (text != null && text.contains(searchText)) {
                                int pos = text.indexOf(searchText) + searchText.length();
                                String beforeText = text.substring(0, pos);
                                String afterText = text.substring(pos);

                                run.setText(beforeText, 0);  // 设置前半部分的文本
                                XWPFRun newRun = paragraph.insertNewRun(i + 1);
                                newRun.addPicture(is, XWPFDocument.PICTURE_TYPE_PNG, sealFile, Units.toEMU(136), Units.toEMU(136));
                                newRun = paragraph.insertNewRun(i + 2);
                                newRun.setText(afterText);  // 设置后半部分的文本
                                pictureInserted = true;
                                break;
                            }
                        }
                        if (pictureInserted) {
                            break;
                        }
                    }
                    if (pictureInserted) {
                        break;
                    }
                }
                if (pictureInserted) {
                    break;
                }
            }
            if (pictureInserted) {
                break;
            }
        }

        if (!pictureInserted) {
            System.out.println("在文档的表格中未找到指定的文本: " + searchText);
        }

        document.write(out);
        out.close();
        is.close();
        document.close();
    }

    @SneakyThrows
    private static void addPicToFile(String fileName, int lineIndex) {
        String sealFile = SEAL_PATH;
        String docPath = SELL_FOLD_PATH + "/" + fileName;
        XWPFDocument document = new XWPFDocument(new FileInputStream(docPath));
        FileOutputStream out = new FileOutputStream(docPath);
        FileInputStream is = new FileInputStream(sealFile);

        int currentLine = 0;
        boolean pictureInserted = false;

        for (XWPFParagraph paragraph : document.getParagraphs()) {
            String[] lines = paragraph.getText().split("\n");
            for (String line : lines) {
                if (currentLine == lineIndex) {
                    XWPFRun run = paragraph.createRun();
                    run.addPicture(is, XWPFDocument.PICTURE_TYPE_PNG, sealFile, Units.toEMU(136), Units.toEMU(136));
                    pictureInserted = true;
                    break;
                }
                currentLine++;
            }
            if (pictureInserted) {
                break;
            }
        }

        if (!pictureInserted) {
            System.out.println("文档中的行数不足 " + (lineIndex + 1) + " 行。");
        }

        document.write(out);
        out.close();
        is.close();
        document.close();
    }

    @SneakyThrows
    private static void addPicToFileHasTable(String fileName, int lineIndex) {
        String sealFile = SEAL_PATH;
        String docPath = SELL_FOLD_PATH + "/" + fileName;
        XWPFDocument document = new XWPFDocument(new FileInputStream(docPath));
        FileOutputStream out = new FileOutputStream(docPath);
        FileInputStream is = new FileInputStream(sealFile);
        int currentLine = 0;
        boolean pictureInserted = false;
        // 只处理第一个表格
        if (!document.getTables().isEmpty()) {
            XWPFTable table = document.getTables().get(0);
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        String[] lines = paragraph.getText().split("\n");
                        for (String line : lines) {
                            if (currentLine == lineIndex) {
                                XWPFRun run = paragraph.createRun();
                                run.addPicture(is, XWPFDocument.PICTURE_TYPE_PNG, sealFile, Units.toEMU(136), Units.toEMU(136));
                                pictureInserted = true;
                                break;
                            }
                            currentLine++;
                        }
                        if (pictureInserted) {
                            break;
                        }
                    }
                    if (pictureInserted) {
                        break;
                    }
                }
                if (pictureInserted) {
                    break;
                }
            }
        }
        if (!pictureInserted) {
            System.out.println("文档中的行数不足 " + (lineIndex + 1) + " 行。");
        }
        document.write(out);
        out.close();
        is.close();
        document.close();
    }

    @SneakyThrows
    private static void copyFile(String filepath, String directFoldPath) {
        Path sourcePath = Paths.get(filepath);
        Path destinationPath = Paths.get(directFoldPath, sourcePath.getFileName().toString());
        if (!Files.exists(sourcePath)) {
            System.out.println("源文件不存在: " + sourcePath);
            return;
        }
        if (Files.exists(destinationPath)) {
            System.out.println("目标文件已存在: " + destinationPath);
            return;
        }
        Files.copy(sourcePath, destinationPath);
    }

    @SneakyThrows
    private static String createFolder(String path, String folderName) {
        String folderPath = path + "/" + folderName;
        File directory = new File(folderPath);
        deleteFolder(path, folderName);
        directory.mkdirs();
        return folderPath;
    }

    @SneakyThrows
    private static String deleteFolder(String path, String folderName) {
        String folderPath = path + "/" + folderName;
        File directory = new File(folderPath);
        if (directory.exists()) {
            FileUtils.deleteDirectory(directory);
        }
        return folderPath;
    }


    @SneakyThrows
    private static List<PatentQuotationDTO> readQuotationExcel(String quotationFilePath) {
        List<PatentQuotationDTO> patentQuotationList = new ArrayList<>();
        FileInputStream fis = new FileInputStream(quotationFilePath);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();
        if (rowIterator.hasNext()) {
            rowIterator.next();
        }
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            PatentQuotationDTO patentQuotationDTO = new PatentQuotationDTO();
            String patentId = getCellValueAsString(row.getCell(0));
            if (StringUtils.hasLength(patentId)) {
                patentQuotationDTO.setPatentId(patentId);
                patentQuotationDTO.setName(getCellValueAsString(row.getCell(1)));
                patentQuotationList.add(patentQuotationDTO);
            }
        }
        return patentQuotationList;
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((int) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

}
