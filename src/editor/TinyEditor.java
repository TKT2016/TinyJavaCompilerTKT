package editor;

import tools.FileUtil;
import jtinyc.Main;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.undo.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.datatransfer.*;

public class TinyEditor extends JFrame implements ActionListener,DocumentListener {
    public final String editorName ="jtiny语言编译运行简单工具";

    JButton btnOpen;
    JButton btnRun1;
    JButton btnRunLex;
    JButton btnRun2;

    JToolBar tb = new JToolBar();

    JMenu fileMenu, editMenu, helpMenu;

    JPopupMenu popupMenu;
    JMenuItem menu_Undo, menu_Cut, menu_Copy, menu_Paste, menu_Delete, menu_SelectAll;
    
    JMenuItem fileMenu_New, fileMenu_Open, fileMenu_Save, fileMenu_SaveAs, fileMenu_Exit;

    JMenuItem editMenu_Undo, editMenu_Cut, editMenu_Copy, editMenu_Paste, editMenu_Delete, editMenu_SelectAll;

    JMenuItem  helpMenu_About;
  
    JTextArea editArea;

    JLabel statusLabel;
   
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Clipboard clipBoard = toolkit.getSystemClipboard();
  
    protected UndoManager undo = new UndoManager();
    protected UndoableEditListener undoHandler = new UndoHandler();
  
    boolean isNewFile = true;//是否新文件(未保存过的)
    File currentFile;//当前文件名

    public TinyEditor() {
        super("jtiny编辑器");

        btnRun1 = new JButton("运行四则运算");
        btnRun1.addActionListener(this);

        btnOpen = new JButton("打开源文件");
        btnOpen.addActionListener(this);

        btnRun2 = new JButton("编译运行jtiny");
        btnRun2.addActionListener(this);

        btnRunLex = new JButton("词法分析");
        btnRunLex.addActionListener(this);

        tb.add(btnRun1);
        tb.addSeparator();
        tb.add(btnOpen);

        tb.add(btnRun2);
        tb.add(btnRunLex);

        tb.setFloatable(false);

        //改变系统默认字体
        Font font = new Font("Dialog", Font.PLAIN, 14);
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, font);
            }
        }
       
        JMenuBar menuBar = new JMenuBar();
        
        fileMenu = new JMenu("文件(F)");
        fileMenu.setMnemonic('F');//设置快捷键ALT+F

        fileMenu_New = new JMenuItem("新建(N)");
        fileMenu_New.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        fileMenu_New.addActionListener(this);

        fileMenu_Open = new JMenuItem("打开(O)...");
        fileMenu_Open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        fileMenu_Open.addActionListener(this);

        fileMenu_Save = new JMenuItem("保存(S)");
        fileMenu_Save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        fileMenu_Save.addActionListener(this);

        fileMenu_SaveAs = new JMenuItem("另存为(A)...");
        fileMenu_SaveAs.addActionListener(this);

        fileMenu_Exit = new JMenuItem("退出(X)");
        fileMenu_Exit.addActionListener(this);

        //创建编辑菜单及菜单项并注册事件监听
        editMenu = new JMenu("编辑(E)");
        editMenu.setMnemonic('E');//设置快捷键ALT+E
        
        //当选择编辑菜单时，设置剪切、复制、粘贴、删除等功能的可用性
        editMenu.addMenuListener(new MenuListener() {
            public void menuCanceled(MenuEvent e)//取消菜单时调用
            {
                checkMenuItemEnabled();//设置剪切、复制、粘贴、删除等功能的可用性
            }

            public void menuDeselected(MenuEvent e)//取消选择某个菜单时调用
            {
                checkMenuItemEnabled();//设置剪切、复制、粘贴、删除等功能的可用性
            }

            public void menuSelected(MenuEvent e)//选择某个菜单时调用
            {
                checkMenuItemEnabled();//设置剪切、复制、粘贴、删除等功能的可用性
            }
        });

        editMenu_Undo = new JMenuItem("撤销(U)");
        editMenu_Undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
        editMenu_Undo.addActionListener(this);
        editMenu_Undo.setEnabled(false);

        editMenu_Cut = new JMenuItem("剪切(T)");
        editMenu_Cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
        editMenu_Cut.addActionListener(this);

        editMenu_Copy = new JMenuItem("复制(C)");
        editMenu_Copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
        editMenu_Copy.addActionListener(this);

        editMenu_Paste = new JMenuItem("粘贴(P)");
        editMenu_Paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
        editMenu_Paste.addActionListener(this);

        editMenu_Delete = new JMenuItem("删除(D)");
        editMenu_Delete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        editMenu_Delete.addActionListener(this);


        editMenu_SelectAll = new JMenuItem("全选", 'A');
        editMenu_SelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
        editMenu_SelectAll.addActionListener(this);


        //创建帮助菜单及菜单项并注册事件监听
        helpMenu = new JMenu("帮助(H)");
        helpMenu.setMnemonic('H');//设置快捷键ALT+H

        helpMenu_About = new JMenuItem("关于编辑器(A)");
        helpMenu_About.addActionListener(this);

        //向菜单条添加"文件"菜单及菜单项
        menuBar.add(fileMenu);
        fileMenu.add(fileMenu_New);
        fileMenu.add(fileMenu_Open);
        fileMenu.add(fileMenu_Save);
        fileMenu.add(fileMenu_SaveAs);
        fileMenu.addSeparator(); 

        fileMenu.add(fileMenu_Exit);

        menuBar.add(editMenu);
        editMenu.add(editMenu_Undo);
        editMenu.addSeparator(); 
        editMenu.add(editMenu_Cut);
        editMenu.add(editMenu_Copy);
        editMenu.add(editMenu_Paste);
        editMenu.add(editMenu_Delete);
        editMenu.addSeparator(); 

        editMenu.add(editMenu_SelectAll);

        //向菜单条添加"帮助"菜单及菜单项
        menuBar.add(helpMenu);
        helpMenu.add(helpMenu_About);

        //向窗口添加菜单条
        this.setJMenuBar(menuBar);

        //创建文本编辑区并添加滚动条
        editArea = new JTextArea(20, 50);
        JScrollPane scroller = new JScrollPane(editArea);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.add(scroller, BorderLayout.CENTER);//向窗口添加文本编辑区
        editArea.setWrapStyleWord(true);//设置单词在一行不足容纳时换行
        editArea.setLineWrap(true);//设置文本编辑区自动换行默认为true,即会"自动换行"

        //编辑区注册事件监听(与撤销操作有关)
        editArea.getDocument().addUndoableEditListener(undoHandler);
        editArea.getDocument().addDocumentListener(this);

        //创建右键弹出菜单
        popupMenu = new JPopupMenu();
        menu_Undo = new JMenuItem("撤销(U)");
        menu_Cut = new JMenuItem("剪切(T)");
        menu_Copy = new JMenuItem("复制(C)");
        menu_Paste = new JMenuItem("粘贴(P)");
        menu_Delete = new JMenuItem("删除(D)");
        menu_SelectAll = new JMenuItem("全选(A)");

        menu_Undo.setEnabled(false);

        //向右键菜单添加菜单项和分隔符
        popupMenu.add(menu_Undo);
        popupMenu.addSeparator();
        popupMenu.add(menu_Cut);
        popupMenu.add(menu_Copy);
        popupMenu.add(menu_Paste);
        popupMenu.add(menu_Delete);
        popupMenu.addSeparator();
        popupMenu.add(menu_SelectAll);

        //文本编辑区注册右键菜单事件
        menu_Undo.addActionListener(this);
        menu_Cut.addActionListener(this);
        menu_Copy.addActionListener(this);
        menu_Paste.addActionListener(this);
        menu_Delete.addActionListener(this);
        menu_SelectAll.addActionListener(this);

        //文本编辑区注册右键菜单事件
        editArea.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger())//返回此鼠标事件是否为该平台的弹出菜单触发事件
                {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());//在组件调用者的坐标空间中的位置 X、Y 显示弹出菜单
                }
                checkMenuItemEnabled();//设置剪切，复制，粘贴，删除等功能的可用性
                editArea.requestFocus();//编辑区获取焦点
            }

            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger())//返回此鼠标事件是否为该平台的弹出菜单触发事件
                {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());//在组件调用者的坐标空间中的位置 X、Y 显示弹出菜单
                }
                checkMenuItemEnabled();//设置剪切，复制，粘贴，删除等功能的可用性
                editArea.requestFocus();//编辑区获取焦点
            }
        });//文本编辑区注册右键菜单事件结束

        //创建和添加状态栏
        statusLabel = new JLabel("Jtiny");
        this.add(statusLabel, BorderLayout.SOUTH);//向窗口添加状态栏标签

        //设置窗口在屏幕上的位置、大小和可见性
        this.setLocation(100, 100);
        this.setSize(650, 550);

        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //添加窗口监听器
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exitWindowChoose();
            }
        });
        this.add(tb, BorderLayout.NORTH);

        checkMenuItemEnabled();
        editArea.requestFocus();
    }

    //设置菜单项的可用性：剪切，复制，粘贴，删除功能
    public void checkMenuItemEnabled() {
        String selectText = editArea.getSelectedText();
        if (selectText == null) {
            editMenu_Cut.setEnabled(false);
            menu_Cut.setEnabled(false);
            editMenu_Copy.setEnabled(false);
            menu_Copy.setEnabled(false);
            editMenu_Delete.setEnabled(false);
            menu_Delete.setEnabled(false);
        }
        else {
            editMenu_Cut.setEnabled(true);
            menu_Cut.setEnabled(true);
            editMenu_Copy.setEnabled(true);
            menu_Copy.setEnabled(true);
            editMenu_Delete.setEnabled(true);
            menu_Delete.setEnabled(true);
        }
        //粘贴功能可用性判断
        Transferable contents = clipBoard.getContents(this);
        if (contents == null) {
            editMenu_Paste.setEnabled(false);
            menu_Paste.setEnabled(false);
        }
        else {
            editMenu_Paste.setEnabled(true);
            menu_Paste.setEnabled(true);
        }
    }

    private void saveFileAs() {
        String str = null;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setApproveButtonText("确定");
        fileChooser.setDialogTitle("另存为");
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.CANCEL_OPTION) {
            statusLabel.setText("您没有保存文件");
            return;
        }

        File saveFileName = fileChooser.getSelectedFile();

        if (saveFileName == null || saveFileName.getName().equals("")) {
            JOptionPane.showMessageDialog(this, "不合法的文件名", "不合法的文件名", JOptionPane.ERROR_MESSAGE);
        }
        else {
            try {
                FileUtil.saveText(saveFileName,editArea.getText());

                isNewFile = false;
                currentFile = saveFileName;
                setEditorTitle(saveFileName.getName());
                statusLabel.setText("当前打开文件:" + saveFileName.getAbsoluteFile());
            } catch (IOException ioException) {
            }
        }
    }

    public void setEditorTitle(String name)
    {
        this.setTitle(name + " - "+editorName);
    }

    private boolean saveFile(File file)
    {
        try {
            FileUtil.saveText(file,editArea.getText());
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "保存文件发生异常:"+ex.getMessage(), "保存文件", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
    }

    //关闭窗口时调用
    public void exitWindowChoose() {
        editArea.requestFocus();
        System.exit(0);
    }

    public void actionPerformed(ActionEvent e) { //新建
        if (e.getSource() == fileMenu_New) {
            editArea.requestFocus();
                editArea.replaceRange("", 0, editArea.getText().length());
                statusLabel.setText(" 新建文件");
                setEditorTitle("无标题");
                isNewFile = true;
                undo.discardAllEdits();//撤消所有的"撤消"操作
                editMenu_Undo.setEnabled(false);

        }
        else if (e.getSource() == btnRun1) {
            compileRunCalculator();
        }
        else if (e.getSource() == btnRun2) {
            compileRunJTiny();
        }
        else if (e.getSource() == btnRunLex) {
            scanJTiny();
        }
        else if (e.getSource() == btnOpen) {
            openSourceFile();
        }
        else if (e.getSource() == fileMenu_Open) {
            openSourceFile();
        }
        else if (e.getSource() == fileMenu_Save) {
            editArea.requestFocus();
            if (isNewFile) {
                String str = null;
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setDialogTitle("保存");
                int result = fileChooser.showSaveDialog(this);
                if (result == JFileChooser.CANCEL_OPTION) {
                    statusLabel.setText("您没有选择任何文件");
                    return;
                }
                File saveFileName = fileChooser.getSelectedFile();
                if (saveFileName == null || saveFileName.getName().equals("")) {
                    JOptionPane.showMessageDialog(this, "不合法的文件名", "不合法的文件名", JOptionPane.ERROR_MESSAGE);
                }
                else {
                        saveFile(saveFileName);
                        isNewFile = false;
                        currentFile = saveFileName;
                        //oldValue = editArea.getText();
                        setEditorTitle(saveFileName.getName());
                        statusLabel.setText("当前打开文件：" + saveFileName.getAbsoluteFile());
                }
            }
            else {
                saveFile(currentFile);
            }
        }
        else if (e.getSource() == fileMenu_SaveAs) {
            saveFileAs();
        }

        else if (e.getSource() == fileMenu_Exit) {
            int exitChoose = JOptionPane.showConfirmDialog(this, "确定要退出吗?", "退出提示", JOptionPane.OK_CANCEL_OPTION);
            if (exitChoose == JOptionPane.OK_OPTION) {
                System.exit(0);
            }
            else {
                return;
            }
        }

        else if (e.getSource() == editMenu_Undo || e.getSource() == menu_Undo) {
            editArea.requestFocus();
            if (undo.canUndo()) {
                try {
                    undo.undo();
                } catch (CannotUndoException ex) {
                    System.out.println("Unable to undo:" + ex);
                    //ex.printStackTrace();
                }
            }
            if (!undo.canUndo()) {
                editMenu_Undo.setEnabled(false);
            }
        }

        else if (e.getSource() == editMenu_Cut || e.getSource() == menu_Cut) {
            editArea.requestFocus();
            String text = editArea.getSelectedText();
            StringSelection selection = new StringSelection(text);
            clipBoard.setContents(selection, null);
            editArea.replaceRange("", editArea.getSelectionStart(), editArea.getSelectionEnd());
            checkMenuItemEnabled();//设置剪切，复制，粘贴，删除功能的可用性
        }
        //复制
        else if (e.getSource() == editMenu_Copy || e.getSource() == menu_Copy) {
            editArea.requestFocus();
            String text = editArea.getSelectedText();
            StringSelection selection = new StringSelection(text);
            clipBoard.setContents(selection, null);
            checkMenuItemEnabled();//设置剪切，复制，粘贴，删除功能的可用性
        }
        //粘贴
        else if (e.getSource() == editMenu_Paste || e.getSource() == menu_Paste) {
            editArea.requestFocus();
            Transferable contents = clipBoard.getContents(this);
            if (contents == null) return;
            String text = "";
            try {
                text = (String) contents.getTransferData(DataFlavor.stringFlavor);
            } catch (Exception exception) {
            }
            editArea.replaceRange(text, editArea.getSelectionStart(), editArea.getSelectionEnd());
            checkMenuItemEnabled();
        }
        //删除
        else if (e.getSource() == editMenu_Delete || e.getSource() == menu_Delete) {
            editArea.requestFocus();
            editArea.replaceRange("", editArea.getSelectionStart(), editArea.getSelectionEnd());
            checkMenuItemEnabled(); //设置剪切、复制、粘贴、删除等功能的可用性
        }
        else if (e.getSource() == editMenu_SelectAll || e.getSource() == menu_SelectAll) {
            editArea.selectAll();
        }
        else if (e.getSource() == helpMenu_About) {
            editArea.requestFocus();
            JOptionPane.showMessageDialog(this,
                    "jtiny语言编译运行简单工具\n" +
                            " 编写者：公悟盐 \n" +
                            " E-mail：gongwuyan@outlook.com    \n" ,
                    "jtiny语言编译运行简单工具", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void openChooserFile() {
        JFileChooser fileChooser = new JFileChooser(FileUtil.getCurrentPath() + "/test");
        if (fileChooser.getSelectedFile() != null)
            fileChooser.setCurrentDirectory(fileChooser.getSelectedFile());
        else
            fileChooser.setCurrentDirectory(new File("./samples"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        fileChooser.setDialogTitle("打开文件");
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.CANCEL_OPTION) {
            statusLabel.setText("您没有选择任何文件");
            return;
        }
        File fileName = fileChooser.getSelectedFile();
        if (fileName == null || fileName.getName().equals("")) {
            JOptionPane.showMessageDialog(this, "不合法的文件名", "不合法的文件名", JOptionPane.ERROR_MESSAGE);
        }
        else {
            try {
                String sourceText = FileUtil.readText(fileName.getAbsolutePath());
                editArea.setText(sourceText);
                setEditorTitle(fileName.getName());
                statusLabel.setText(" 当前打开文件：" + fileName.getAbsoluteFile());

                isNewFile = false;
                currentFile = fileName;
                //oldValue = editArea.getText();
            } catch (IOException ioException) {
            }
        }
    }

    private void openSourceFile() {
        openChooserFile();
    }

    private void compileRunJTiny() {
        if (this.currentFile == null) {
            JOptionPane.showMessageDialog(this, "没有打开源文件", "错误", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Main.compileRun(currentFile.getAbsolutePath(),false);
    }

    private void scanJTiny() {
        if (this.currentFile == null) {
            JOptionPane.showMessageDialog(this, "没有打开源文件", "错误", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Main.scan(currentFile.getAbsolutePath());
    }

    private void compileRunCalculator() {
        CalculatorEditor calculatorCodeFrame = new CalculatorEditor();
        calculatorCodeFrame.show();
    }

    public void removeUpdate(DocumentEvent e) {
        editMenu_Undo.setEnabled(true);
    }

    public void insertUpdate(DocumentEvent e) {
        editMenu_Undo.setEnabled(true);
    }

    public void changedUpdate(DocumentEvent e) {
        editMenu_Undo.setEnabled(true);
    }

    class UndoHandler implements UndoableEditListener {
        public void undoableEditHappened(UndoableEditEvent uee) {
            undo.addEdit(uee.getEdit());
        }
    }

    public static void main(String args[]) {
        TinyEditor pad = new TinyEditor();
        pad.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

