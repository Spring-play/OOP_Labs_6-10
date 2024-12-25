package oop.StoreAdmin;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.Transformer;
import org.apache.log4j.Logger;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.NodeList;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;

import javax.xml.transform.stream.StreamResult;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import javax.swing.*;
import java.io.File;
import java.awt.*;

public class AppStore {
    
    
    // Объявления графических компонентов
	private JScrollPane scrollProducts, scrollSellers, scrollCategories; 
	private JComboBox<String> searchCriteria;
    private JTextField searchField; 
    private JFrame mainFrame; 
    private JButton btnSearch; 
    private DefaultTableModel productTableModel, sellerTableModel, categoryTableModel; 
    private String[][] originalProductData, originalSellerData, originalCategoryData; 
    private JButton btnSave, btnLoad, btnDelete, btnClear, btnRegisterSeller, btnAddRow,HTMLOT; 
    private JTable productTable, sellerTable, categoryTable; 
    private static final Logger logger = Logger.getLogger(AppStore.class);
    

    private void createMainFrame() {
        mainFrame = new JFrame("Администрирование магазина");
        mainFrame.setSize(800, 500);
        mainFrame.setLocation(250, 80);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public void show() {
        createMainFrame();
        createToolBar();
        createTabbedPane();
        createSearchPanel();
        mainFrame.setVisible(true);
        logger.info("Приложение запущено");
    }


    private void createToolBar() {
        JToolBar toolBar = new JToolBar(JToolBar.VERTICAL);
        toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.Y_AXIS));
        
        // Создание кнопок
        btnSave = createButton("./img/save.png", "Сохранить данные");
        btnAddRow = createButton("./img/row.png", "Добавить строку");
        btnLoad = createButton("./img/loading.png", "Загрузить данные из XML");
        btnDelete = createButton("./img/delit.png", "Удалить строку");
        btnClear = createButton("./img/Trash.png", "Очистить таблицу");
        btnRegisterSeller = createButton("./img/User.png", "Регистрация продавца");
        HTMLOT = createButton("./img/html.png", "hml отчет");
        // Установка цвета фона для кнопок
        setButtonBackgroundColor(btnSave, btnAddRow, btnLoad, btnDelete, btnClear, btnRegisterSeller,HTMLOT);
        
        // Добавление кнопок в панель инструментов
        toolBar.add(btnLoad);
        toolBar.add(btnAddRow);
        toolBar.add(btnDelete);
        toolBar.add(btnSave);
        toolBar.add(btnClear);
        toolBar.add(btnRegisterSeller);
        toolBar.add(HTMLOT);
        
        // Добавление обработчиков событий для кнопок
        addEventListeners();
        
        // Размещение панели инструментов
        mainFrame.setLayout(new BorderLayout());
        mainFrame.add(toolBar, BorderLayout.EAST);
    }

    private JButton createButton(String iconPath, String toolTip) {
        JButton button = new JButton(new ImageIcon(iconPath));
        button.setPreferredSize(new Dimension(40, 30));
        button.setToolTipText(toolTip);
        return button;
    }

    private void setButtonBackgroundColor(JButton... buttons) {
        for (JButton button : buttons) {
            button.setBackground(Color.GREEN);
        }
    }

    private void addEventListeners() {
        btnSave.addActionListener(e -> saveData());
        btnLoad.addActionListener(e -> loadData());
        btnDelete.addActionListener(e -> deleteSelectedRow());
        btnClear.addActionListener(e -> clearTableData());
        btnRegisterSeller.addActionListener(e -> showRegistrationDialog());
        btnAddRow.addActionListener(e -> addEmptyRow());
        HTMLOT.addActionListener(e -> generateHTMLReport());
    }

    private void createTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        createTables();
        
        // Добавление вкладок
        tabbedPane.addTab("Товары", scrollProducts);
        tabbedPane.addTab("Продавцы", scrollSellers);
        tabbedPane.addTab("Категории", scrollCategories);
        
        // Добавление панели вкладок в основное окно
        // Добавление панели вкладок в основное окно
        mainFrame.add(tabbedPane, BorderLayout.CENTER);
    }

    private void createTables() {
        String[] productColumns = {"Название_товара", "Цена", "Количество"};
        productTableModel = new DefaultTableModel(new String[][]{}, productColumns);
        productTable = new JTable(productTableModel);
        scrollProducts = new JScrollPane(productTable);
        
        String[] sellerColumns = {"Имя_продавца", "Контакт"};
        sellerTableModel = new DefaultTableModel(new String[][]{}, sellerColumns);
        sellerTable = new JTable(sellerTableModel);
        scrollSellers = new JScrollPane(sellerTable);
        
        String[] categoryColumns = {"Название_категории", "Описание"};
        categoryTableModel = new DefaultTableModel(new String[][]{}, categoryColumns);
        categoryTable = new JTable(categoryTableModel);
        scrollCategories = new JScrollPane(categoryTable);
    }

    private void createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchCriteria = new JComboBox<>(new String[]{"Товары", "Продавцы", "Категории"});
        searchField = new JTextField("                                  ");
        btnSearch = new JButton("Поиск");
        JButton btnReset = new JButton("Возврат к исходному"); // Кнопка сброса

        // Создаем панель для размещения компонентов поиска
        JPanel innerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        innerPanel.add(searchCriteria);
        innerPanel.add(searchField);
        innerPanel.add(btnSearch);
        innerPanel.add(btnReset); // Добавляем кнопку сброса

        // Добавляем внутреннюю панель в верхнюю панель
        searchPanel.add(innerPanel, BorderLayout.CENTER);

        btnSearch.addActionListener(e -> performSearch());
        btnReset.addActionListener(e -> resetSearch()); // Обработчик для кнопки сброса
        
        

        mainFrame.add(searchPanel, BorderLayout.NORTH);
    }
    private void resetSearch() {
        // Очищаем текущие данные в таблицах
        productTableModel.setRowCount(0);
        sellerTableModel.setRowCount(0);
        categoryTableModel.setRowCount(0);

        // Восстанавливаем данные в таблицах
        if (originalProductData != null) {
            for (String[] row : originalProductData) {
                productTableModel.addRow(row);
            }
        }
        if (originalSellerData != null) {
            for (String[] row : originalSellerData) {
                sellerTableModel.addRow(row);
            }
        }
        if (originalCategoryData != null) {
            for (String[] row : originalCategoryData) {
                categoryTableModel.addRow(row);
            }
        }

        // Очищаем поле поиска
        searchField.setText("Поиск по слову");
        searchCriteria.setSelectedIndex(0); // Сбрасываем выбор критерия поиска
    }

    private void deleteSelectedRow() {
        int selectedTab = ((JTabbedPane) mainFrame.getContentPane().getComponent(1)).getSelectedIndex();
        JTable selectedTable;
        DefaultTableModel selectedModel;
        
        switch (selectedTab) {
            case 0:
                selectedTable = productTable;
                selectedModel = productTableModel;
                break;
            case 1:
                selectedTable = sellerTable;
                selectedModel = sellerTableModel;
                break;
            case 2:
                selectedTable = categoryTable;
                selectedModel = categoryTableModel;
                break;
            default:
                return;
        }
        
        int selectedRow = selectedTable.getSelectedRow();
        if (selectedRow != -1) {
            selectedModel.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Выберите строку для удаления");
        }
    }

    private void clearTableData() {
        int selectedTab = ((JTabbedPane) mainFrame.getContentPane().getComponent(1)).getSelectedIndex();
        DefaultTableModel selectedModel;
        
        switch (selectedTab) {
            case 0:
                selectedModel = productTableModel;
                break;
            case 1:
                selectedModel = sellerTableModel;
                break;
            case 2:
                selectedModel = categoryTableModel;
                break;
            default:
                return;
        }
        
        selectedModel.setRowCount(0); // Очистка таблицы
    }

    private void addEmptyRow() {
        int selectedTab = ((JTabbedPane) mainFrame.getContentPane().getComponent(1)).getSelectedIndex();
        DefaultTableModel selectedModel;
        
        switch (selectedTab) {
            case 0:
                selectedModel = productTableModel;
                break;
            case 1:
                selectedModel = sellerTableModel;
                break;
            case 2:
                selectedModel = categoryTableModel;
                break;
            default:
                return;
        }
        
        selectedModel.addRow(new String[selectedModel.getColumnCount()]); // Добавляем пустую строку
    }

    private void showRegistrationDialog() {
        JDialog registrationDialog = new JDialog(mainFrame, "Регистрация продавца", true);
        registrationDialog.setSize(300, 200);
        registrationDialog.setLayout(new BorderLayout());
        
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 2));
        JLabel nameLabel = new JLabel("Имя:");
        JTextField nameField = new JTextField();
        JLabel contactLabel = new JLabel("Контакт:");
        JTextField contactField = new JTextField();
        
        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(contactLabel);
        inputPanel.add(contactField);
        
        JPanel buttonPanel = new JPanel();
        JButton registerButton = new JButton("Зарегистрировать");
        JButton cancelButton = new JButton("Отмена");
        
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);
        
        registrationDialog.add(inputPanel, BorderLayout.CENTER);
        registrationDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        registerButton.addActionListener(e -> {
            String name = nameField.getText();
            String contact = contactField.getText();
            if (name.isEmpty() || contact.isEmpty()) {
                JOptionPane.showMessageDialog(registrationDialog, "Поля не должны быть пустыми.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Здесь можно добавить логику для сохранения информации о продавце
            JOptionPane.showMessageDialog(registrationDialog, "Регистрация успешна!", "Информация", JOptionPane.INFORMATION_MESSAGE);
            registrationDialog.dispose();
        });

        cancelButton.addActionListener(e -> registrationDialog.dispose());
        registrationDialog.setLocationRelativeTo(mainFrame);
        registrationDialog.setVisible(true);
    }

    private void saveData() {
        logger.info("Сохранение данных.");
        FileDialog saveDialog = new FileDialog(mainFrame, "Сохранение данных", FileDialog.SAVE);
        saveDialog.setFile("store_data.xml");
        saveDialog.setVisible(true);
        String directory = saveDialog.getDirectory();
        String selectedFile = saveDialog.getFile();
        
        if (directory == null || selectedFile == null) {
            logger.warn("Пользователь отменил сохранение.");
            return;
        }
        
        String fileName = directory + selectedFile;
        
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("store");
            doc.appendChild(rootElement);
            
            // Сохранение данных из таблиц
            saveTableData(doc, rootElement, "products", productTableModel);
            saveTableData(doc, rootElement, "sellers", sellerTableModel);
            saveTableData(doc, rootElement, "categories", categoryTableModel);
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(fileName));
            transformer.transform(source, result);
            
            logger.info("Данные успешно сохранены в " + fileName);
            JOptionPane.showMessageDialog(mainFrame, "Данные успешно сохранены в " + fileName, "Успех", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Ошибка при сохранении данных: " + e.getMessage());
            JOptionPane.showMessageDialog(mainFrame, "Ошибка при сохранении данных: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadData() {
        logger.info("Загрузка данных из XML.");
        FileDialog loadDialog = new FileDialog(mainFrame, "Загрузка данных", FileDialog.LOAD);
        loadDialog.setVisible(true);
        String directory = loadDialog.getDirectory();
        String selectedFile = loadDialog.getFile();
        
        if (directory == null || selectedFile == null) {
            logger.warn("Пользователь отменил загрузку.");
            return;
        }
        
        String fileName = directory + selectedFile;
        
        try {
            File xmlFile = new File(fileName);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            
            // Очистка текущих данных в таблицах перед загрузкой новых
            productTableModel.setRowCount(0);
            sellerTableModel.setRowCount(0);
            categoryTableModel.setRowCount(0);
            
            loadTableData(doc, "products", productTableModel);
            loadTableData(doc, "sellers", sellerTableModel);
            loadTableData(doc, "categories", categoryTableModel);
            
            // Сохраняем исходные данные
            originalProductData = getTableData(productTableModel);
            originalSellerData = getTableData(sellerTableModel);
            originalCategoryData = getTableData(categoryTableModel);
            
            logger.info("Данные успешно загружены из " + fileName);
            JOptionPane.showMessageDialog(mainFrame, "Данные успешно загружены из " + fileName, "Успех", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Ошибка при загрузке данных: " + e.getMessage());
            JOptionPane.showMessageDialog(mainFrame, "Ошибка при загрузке данных: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTableData(Document doc, String tagName, DefaultTableModel model) {
        NodeList nodeList = doc.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            NodeList rowList = nodeList.item(0).getChildNodes();
            for (int i = 0; i < rowList.getLength(); i++) {
                Node rowNode = rowList.item(i);
                if (rowNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element rowElement = (Element) rowNode;
                    String[] rowData = new String[model.getColumnCount()];
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        String columnName = model.getColumnName(j);
                        NodeList cellNodes = rowElement.getElementsByTagName(columnName);
                        if (cellNodes.getLength() > 0) {
                            rowData[j] = cellNodes.item(0).getTextContent();
                        } else {
                            rowData[j] = ""; // Если нет данных, добавляем пустую строку
                        }
                    }
                    model.addRow(rowData);
                }
            }
        }
    }

    private void saveTableData(Document doc, Element rootElement, String tagName, DefaultTableModel model) {
        Element tableElement = doc.createElement(tagName);
        rootElement.appendChild(tableElement);
        for (int i = 0; i < model.getRowCount(); i++) {
            Element rowElement = doc.createElement("row");
            tableElement.appendChild(rowElement);
            for (int j = 0; j < model.getColumnCount(); j++) {
                Element cellElement = doc.createElement(model.getColumnName(j));
                cellElement.appendChild(doc.createTextNode((String) model.getValueAt(i, j)));
                rowElement.appendChild(cellElement);
            }
        }
    }

    private void performSearch() {
        String keyword = searchField.getText().trim().toLowerCase();
        logger.debug("Поиск по слову: " + keyword);
        int selectedIndex = searchCriteria.getSelectedIndex();
        DefaultTableModel model;
        String[][] originalData;

        switch (selectedIndex) {
            case 0: // Товары
                model = productTableModel;
                originalData = originalProductData != null ? originalProductData : getTableData(model);
                break;
            case 1: // Продавцы
                model = sellerTableModel;
                originalData = originalSellerData != null ? originalSellerData : getTableData(model);
                break;
            case 2: // Категории
                model = categoryTableModel;
                originalData = originalCategoryData != null ? originalCategoryData : getTableData(model);
                break;
            default:
                return;
        }

        model.setRowCount(0); // Очистка текущих данных в таблице
        for (String[] row : originalData) {
            boolean match = false;
            for (String cell : row) {
                if (cell.toLowerCase().contains(keyword)) {
                    match = true;
                    break;
                }
            }
            if (match) {
                model.addRow(row);
            }
        }
    }

    private String[][] getTableData(DefaultTableModel model) {
        int rowCount = model.getRowCount();
        int columnCount = model.getColumnCount();
        String[][] tableData = new String[rowCount][columnCount];
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                tableData[i][j] = (String) model.getValueAt(i, j);
            }
        }
        return tableData;
    }
    private void generateHTMLReport() {
        JDialog dialog = new JDialog(mainFrame, "Выбор вкладок для генерации HTML", true);
        dialog.setSize(300, 200);
        dialog.setLayout(new GridLayout(0, 1)); 
        
        // Чекбоксы для выбора вкладок
        JCheckBox productsCheckBox = new JCheckBox("Товары", true);
        JCheckBox sellersCheckBox = new JCheckBox("Продавцы", true);
        JCheckBox categoriesCheckBox = new JCheckBox("Категории", true);
        
        // Добавление чекбоксов в диалог
        dialog.add(productsCheckBox);
        dialog.add(sellersCheckBox);
        dialog.add(categoriesCheckBox);
        
        // Кнопка для генерации HTML
        JButton generateButton = new JButton("Сгенерировать HTML");
        dialog.add(generateButton);
        
        // Обработчик события для кнопки генерации
        generateButton.addActionListener(e -> {
            try {
                // Генерация отчетов для выбранных вкладок
                if (productsCheckBox.isSelected() && productTable.getRowCount() > 0) {
                    generateHTMLReportForTable("K:\\eclipse\\ecl_artifact\\kyrsach_3sem_pavel\\BookList\\pr.jasper", productTableModel, "products_report.html");
                }
                if (sellersCheckBox.isSelected() && sellerTable.getRowCount() > 0) {
                    generateHTMLReportForTable("K:\\\\eclipse\\\\ecl_artifact\\\\kyrsach_3sem_pavel\\\\BookList\\\\sl.jasper", sellerTableModel, "sellers_report.html");
                }
                if (categoriesCheckBox.isSelected() && categoryTable.getRowCount() > 0) {
                    generateHTMLReportForTable("K:\\\\eclipse\\\\ecl_artifact\\\\kyrsach_3sem_pavel\\\\BookList\\\\ct.jasperr", categoryTableModel, "categories_report.html");
                }

                JOptionPane.showMessageDialog(mainFrame, "HTML отчеты успешно сгенерированы!", "Успех", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(mainFrame, "Ошибка при генерации HTML отчета: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Установка позиции диалога относительно основного окна
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true);
    }

    private void generateHTMLReportForTable(String reportPath, DefaultTableModel model, String outputFileName) throws JRException {
        // Создание источника данных для отчета
        JRTableModelDataSource dataSource = new JRTableModelDataSource(model);
        
        // Заполнение отчета
        JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, null, dataSource);
        
        // Экспорт отчета в HTML файл
        JasperExportManager.exportReportToHtmlFile(jasperPrint, outputFileName);
    }

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AppStore().show());
    }
}