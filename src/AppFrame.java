import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;

public class AppFrame extends JFrame implements ActionListener, Runnable{
    //palette
    private static final Color colorBackGround=new Color(222, 222, 222);
    private static final Color colorTextImageDirectory=new Color(107, 156, 190);
    private static final Color colorButtonConvert=new Color(26, 208, 39);
    private static final Color colorImgLabel=new Color(182, 196, 241);

    //campi statici
    private static final String[] supportedConversionFormats={"txt","png g s","png col 1", "png col 2"};
    private static final String appIconPath = "images/app logo.png";


    //campi non statici
    private static final int frameWidth=800,frameHeight=600;
    private final Container c;
    private JMenuBar menuBar;
    private JMenu menuFile;
    private JMenuItem menuItemSelectPhoto,menuItemExit;
    private JScrollPane sPTextImageDirectory,sPTextCustomPalette;
    private JTextArea textImageDirectory,textInstrSelectedPhoto,textResolutions;
    private JTextArea textInstrConversionRateo1,textInstrConversionRateo2,textInstrConversionRateo3;
    private JTextArea textInstrSelectPalette,textInstrConversionFormate,textCustomPalette;
    JButton buttonImageDirectory;//invisibile e sovrapposto con scritta directory
    private JRadioButton[] rButtonsConversionFormate,rButtonsCharPalette;
    private ButtonGroup bGroupConversionFormate,bGroupCharPalette;
    private JFileChooser fileChooser,folderChooser;
    private JButton buttonConvert,buttonMoreRateo,buttonLessRateo;
    private JCheckBox checkBoxInverted;
    private JLabel imgLabel;

    private File selectedImage,conversionFile;
   private AsciiConverter converter;
   private char[][] asciiMatrix;


    //costruttore
    AppFrame(){
        super("v3.0 - convertitore ascii art");
        c = getContentPane();
        c.setLayout(null);
        c.setBackground(colorBackGround);
        //c.setBackground(Color.black); //debug
        selectedImage=null;

        converter=new AsciiConverter();
        asciiMatrix=null;


        //ordine importante
        buildMenuBar();
        buildDirectoriesTexts();
        buildRButtonsCharPalette();
        buildRButtonsTexts();
        buildRButtonsConversionFormate();
        buildCustomPaletteThings();
        buildResolutionIndicators();
        buildScrollPanels();
        buildButtons();
        buildCheckBoxInverted();
        buildImgLabel();

        buildFileChooser();
        buildFolderChooser();

        addComponents();

        //compilo testi (e posiziono componenti)
        updateResolutionInfo();


        setBounds((getToolkit().getScreenSize().width-frameWidth)/2,(getToolkit().getScreenSize().height-frameHeight)/2,frameWidth,frameHeight);
        setVisible(true);
        setResizable(false);
        setIconImage(new ImageIcon(appIconPath).getImage());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        new Thread(this).start();
    }


    //metodi interni
    private void buildMenuBar(){
        menuItemSelectPhoto=new JMenuItem("Select photo");
        menuItemSelectPhoto.setMnemonic(KeyEvent.VK_P);
        menuItemSelectPhoto.addActionListener(this);
        menuItemExit=new JMenuItem("Exit");
        menuItemExit.setMnemonic(KeyEvent.VK_X);
        menuItemExit.addActionListener(this);

        menuFile=new JMenu("File");
        menuFile.setMnemonic(KeyEvent.VK_F);
        menuFile.add(menuItemSelectPhoto);
        menuFile.addSeparator();
        menuFile.add(menuItemExit);

        menuBar=new JMenuBar();
        menuBar.add(menuFile);
    }
    private void buildDirectoriesTexts(){
        //testo istruzioni foto selezionata
        textInstrSelectedPhoto=new JTextArea("selected photo:");
        textInstrSelectedPhoto.setBounds(10,10,90,20);
        textInstrSelectedPhoto.setEnabled(false);
        textInstrSelectedPhoto.setDisabledTextColor(Color.BLACK);
        textInstrSelectedPhoto.setOpaque(false);

        //testo directory immagine scelta
        textImageDirectory=new JTextArea("");
        textImageDirectory.setDisabledTextColor(colorTextImageDirectory);
        textImageDirectory.setEnabled(false);
        textInstrSelectedPhoto.setOpaque(false);

    }
    private void buildCustomPaletteThings(){
        textCustomPalette=new JTextArea("");
        //textCustomPalette.setEnabled(false);
        textCustomPalette.setDisabledTextColor(Color.BLACK);
        textCustomPalette.setOpaque(false);

        sPTextCustomPalette=new JScrollPane(textCustomPalette);
        sPTextCustomPalette.setBounds(rButtonsCharPalette[rButtonsCharPalette.length-1].getX()+rButtonsCharPalette[rButtonsCharPalette.length-1].getWidth()+20,rButtonsCharPalette[rButtonsCharPalette.length-1].getY()+5,180,textInstrSelectPalette.getHeight()+3);
        sPTextCustomPalette.setVisible(false);
    }
    private void buildRButtonsTexts(){

        //testo instr per selezionare palette
        textInstrSelectPalette=new JTextArea("select a palette to use:");
        textInstrSelectPalette.setBounds(10,40,textInstrSelectPalette.getPreferredSize().width,textInstrSelectPalette.getPreferredSize().height);
        textInstrSelectPalette.setEnabled(false);
        textInstrSelectPalette.setDisabledTextColor(Color.BLACK);
        textInstrSelectPalette.setOpaque(false);

        //testo instr per selezionare formato di conversione
        textInstrConversionFormate=new JTextArea("select conversion formate:");
        textInstrConversionFormate.setBounds(10,230,textInstrConversionFormate.getPreferredSize().width,textInstrConversionFormate.getPreferredSize().height);
        textInstrConversionFormate.setEnabled(false);
        textInstrConversionFormate.setDisabledTextColor(Color.BLACK);
        textInstrConversionFormate.setOpaque(false);
    }
    private void buildScrollPanels(){
        //barra scorrimento per testo directory immagine scelta
        sPTextImageDirectory=new JScrollPane(textImageDirectory);
        sPTextImageDirectory.setBounds(textInstrSelectedPhoto.getX()+textInstrSelectedPhoto.getWidth(),textInstrSelectedPhoto.getY(),260,textInstrSelectedPhoto.getHeight());

    }
    private void buildRButtonsCharPalette(){
        bGroupCharPalette=new ButtonGroup();

        rButtonsCharPalette=new JRadioButton[AsciiConverter.PALETTES.length+1];//+1 per opzione palette custom

        JRadioButton curButton;
        int buttonsX=10;
        for(int i=0;i<rButtonsCharPalette.length;i++){
            if(i<rButtonsCharPalette.length-1){
                curButton=new JRadioButton(AsciiConverter.PALETTES[i]);
            }else{
                curButton=new JRadioButton("custom palette");
            }

            //curButton.setSize(curButton.getPreferredSize());
            curButton.setSize(curButton.getPreferredSize().width+10,curButton.getPreferredSize().height);//per qualche motivo necessario il +10
            if(i==0){
                curButton.setLocation(buttonsX,60);
            }else{
                curButton.setLocation(buttonsX,rButtonsCharPalette[i-1].getY()+rButtonsCharPalette[i-1].getHeight()+5);
            }//posizionamento pulsante
            curButton.setOpaque(false);
            curButton.addActionListener(this);
            curButton.setFocusable(false);


            rButtonsCharPalette[i]=curButton;
            bGroupCharPalette.add(rButtonsCharPalette[i]);
        }


    }
    private void buildRButtonsConversionFormate(){
        bGroupConversionFormate=new ButtonGroup();

        rButtonsConversionFormate=new JRadioButton[supportedConversionFormats.length];

        JRadioButton curButton;
        int buttonsY=250;
        for(int i=0;i<rButtonsConversionFormate.length;i++){
            curButton=new JRadioButton(supportedConversionFormats[i]);

            curButton.setSize(curButton.getPreferredSize().width + 2, curButton.getPreferredSize().height);
            if(i==0){
                curButton.setLocation(10,buttonsY);
            }else{
                curButton.setLocation(rButtonsConversionFormate[i-1].getX()+rButtonsConversionFormate[i-1].getWidth(),buttonsY);
            }//posizionamento pulsante
            curButton.setOpaque(false);
            curButton.addActionListener(this);
            curButton.setFocusable(false);


            rButtonsConversionFormate[i]=curButton;
            bGroupConversionFormate.add(rButtonsConversionFormate[i]);
        }
    }
    private void buildFileChooser(){
        fileChooser =new JFileChooser();
        fileChooser.setCurrentDirectory(FileSystemView.getFileSystemView().getDefaultDirectory());
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new FileNameExtensionFilter("supported images","png","bmp"));//nome opzione, estensioni mostrate
    }
    private void buildFolderChooser(){
        folderChooser =new JFileChooser();
        /*
        folderChooser.setCurrentDirectory(FileSystemView.getFileSystemView().getDefaultDirectory());
        folderChooser.setAcceptAllFileFilterUsed(false);
        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //folderChooser.setFileFilter(new FileNameExtensionFilter("",""));//nome opzione, estensioni mostrate
        */
    }
    private void buildButtons(){
        //bottone invisibile per selezione immagine
        buttonImageDirectory =new JButton();
        buttonImageDirectory.setBounds(sPTextImageDirectory.getX(),sPTextImageDirectory.getY(),sPTextImageDirectory.getWidth(),sPTextImageDirectory.getHeight());
        buttonImageDirectory.setOpaque(false);
        buttonImageDirectory.setFocusable(false);
        buttonImageDirectory.setBackground(new Color(0,0,0,0));
        buttonImageDirectory.addActionListener(this);

        //bottone per convertire immagine
        buttonConvert=new JButton("convert");
        buttonConvert.setBounds(40,480,200,40);
        buttonConvert.setHorizontalTextPosition(SwingConstants.CENTER);
        buttonConvert.setVerticalTextPosition(SwingConstants.CENTER);
        buttonConvert.setBackground(colorButtonConvert);
        buttonConvert.setFocusable(false);
        buttonConvert.addActionListener(this);
    }
    private void buildCheckBoxInverted(){
        checkBoxInverted = new JCheckBox("inverted");
        checkBoxInverted.setSize(checkBoxInverted.getPreferredSize().width + 10, checkBoxInverted.getPreferredSize().height);
        checkBoxInverted.setLocation(10, 290);
        checkBoxInverted.setOpaque(false);
        checkBoxInverted.setFocusable(false);

        //inizializzo a selezionato
        if(!checkBoxInverted.isSelected()){
            checkBoxInverted.doClick();
        }



    }
    private void buildResolutionIndicators(){
        //(posizioni e dimensioni all'interno di updateResolutionInfo)

        //inizializzo tutti i componenti
        textResolutions=new JTextArea();
        textInstrConversionRateo1=new JTextArea();
        textInstrConversionRateo2=new JTextArea();
        textInstrConversionRateo3=new JTextArea();
        buttonMoreRateo=new JButton("+");
        buttonLessRateo=new JButton("-");

        //testi istruzioni risoluzione conversione
        textResolutions.setEnabled(false);
        textResolutions.setDisabledTextColor(Color.BLACK);
        textResolutions.setOpaque(false);

        textInstrConversionRateo1.setEnabled(false);
        textInstrConversionRateo1.setDisabledTextColor(Color.BLACK);
        textInstrConversionRateo1.setOpaque(false);

        textInstrConversionRateo2.setEnabled(false);
        textInstrConversionRateo2.setDisabledTextColor(Color.BLACK);
        textInstrConversionRateo2.setOpaque(false);

        textInstrConversionRateo3.setEnabled(false);
        textInstrConversionRateo3.setDisabledTextColor(Color.BLACK);
        textInstrConversionRateo3.setOpaque(false);

        //bottone per aumentare rateo di conversione
        buttonMoreRateo.setHorizontalTextPosition(SwingConstants.CENTER);
        buttonMoreRateo.setVerticalTextPosition(SwingConstants.CENTER);
        buttonMoreRateo.setFocusable(false);
        buttonMoreRateo.addActionListener(this);

        //bottone per aumentare rateo di conversione
        buttonLessRateo.setHorizontalTextPosition(SwingConstants.CENTER);
        buttonLessRateo.setVerticalTextPosition(SwingConstants.CENTER);
        buttonLessRateo.setFocusable(false);
        buttonLessRateo.addActionListener(this);
    }
    private void buildImgLabel(){
        imgLabel = new JLabel();
        imgLabel.setBounds(400,20,350,500);
        imgLabel.setOpaque(true);
        imgLabel.setBackground(colorImgLabel);
        imgLabel.setVerticalAlignment(SwingConstants.CENTER);
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }
    private void addComponents(){
        c.add(buttonImageDirectory);//importante che stia sopra per avere priorità
        setJMenuBar(menuBar);
        c.add(textInstrSelectedPhoto);
        c.add(sPTextImageDirectory);
        c.add(textInstrSelectPalette);
        for(int i=0;i<rButtonsCharPalette.length;i++){
            c.add(rButtonsCharPalette[i]);
        }
        c.add(textInstrConversionFormate);
        for(int i=0;i<rButtonsConversionFormate.length;i++){
            c.add(rButtonsConversionFormate[i]);
        }
        c.add(sPTextCustomPalette);
        c.add(checkBoxInverted);
        c.add(textResolutions);
        c.add(textInstrConversionRateo1);
        c.add(textInstrConversionRateo2);
        c.add(textInstrConversionRateo3);
        c.add(buttonMoreRateo);
        c.add(buttonLessRateo);
        c.add(buttonConvert);
        c.add(imgLabel);
    }
    private void updateResolutionInfo(){
        if(converter.getImage()==null){
            textResolutions.setText("photo pixels:             0x0\nascii art characters: 0x0");
        }else{
            int imageWeight=converter.getImage().getWidth(),imageHeight=converter.getImage().getHeight();
            if(converter.getConversionRateo()==0){
                textResolutions.setText("photo pixels:             "+imageWeight+"x"+imageHeight+"\nascii art characters: 0x0");
            }else{

                textResolutions.setText("photo pixels:             "+imageWeight+"x"+imageHeight+"\nascii art characters: "+converter.getConversionWidth()+"x"+converter.getConversionHeight());
            }
        }

        textInstrConversionRateo1.setText("every character will represent approximately ");
        textInstrConversionRateo2.setText(""+converter.getConversionRateo());
        textInstrConversionRateo3.setText(" pixels");

        //setto dimensioni
        textResolutions.setSize(textResolutions.getPreferredSize());
        textInstrConversionRateo1.setSize(textInstrConversionRateo1.getPreferredSize());
        textInstrConversionRateo2.setSize(textInstrConversionRateo2.getPreferredSize());
        textInstrConversionRateo3.setSize(textInstrConversionRateo3.getPreferredSize());
        buttonLessRateo.setSize(45,26);
        buttonMoreRateo.setSize(45,26);

        //setto posizioni
        textResolutions.setLocation(10,400);
        textInstrConversionRateo1.setLocation(textResolutions.getX(),textResolutions.getY()+textResolutions.getHeight());
        textInstrConversionRateo2.setLocation(textInstrConversionRateo1.getX()+textInstrConversionRateo1.getWidth(),textInstrConversionRateo1.getY());
        textInstrConversionRateo3.setLocation(textInstrConversionRateo2.getX()+textInstrConversionRateo2.getWidth(),textInstrConversionRateo1.getY());
        buttonMoreRateo.setLocation(textInstrConversionRateo2.getX()+(textInstrConversionRateo2.getWidth()-buttonMoreRateo.getWidth())/2,textInstrConversionRateo2.getY()-25-1);
        buttonLessRateo.setLocation(textInstrConversionRateo2.getX()+(textInstrConversionRateo2.getWidth()-buttonLessRateo.getWidth())/2,textInstrConversionRateo2.getY()+textInstrConversionRateo2.getHeight()+1);
    }


    public void actionPerformed(ActionEvent e){
        Object source=e.getSource();

        //creo sotto-finestra di selezione file (migliorare selezione estensioni)
        if(source==menuItemSelectPhoto||source==buttonImageDirectory){

            fileChooser.showOpenDialog(null);//apre la finestra e aspetta per risultato

            //se ho effettivamente scelto una immagine aggiorno cose
            if(fileChooser.getSelectedFile()!=null){
                selectedImage=fileChooser.getSelectedFile();
                if(selectedImage.exists()){
                    textImageDirectory.setText(selectedImage.getPath());

                    //carico BufferedImage in convertitore
                    try{
                        converter.loadImage(ImageIO.read(selectedImage));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                    //setto info definizione immagine
                    updateResolutionInfo();

                    if(converter.getImage().getWidth()/imgLabel.getWidth() >= converter.getImage().getHeight()/imgLabel.getHeight()){
                        imgLabel.setIcon(new ImageIcon(converter.getImage().getScaledInstance(imgLabel.getWidth(),converter.getImage().getHeight()*imgLabel.getWidth()/converter.getImage().getWidth(),BufferedImage.SCALE_DEFAULT)));
                    }else{
                        imgLabel.setIcon(new ImageIcon(converter.getImage().getScaledInstance(converter.getImage().getWidth()*imgLabel.getHeight()/converter.getImage().getHeight(),imgLabel.getHeight(),BufferedImage.SCALE_DEFAULT)));
                    }


                }
            }

            //se c'e bisogno di barra scorrimento aumento altezza
            if(textImageDirectory.getPreferredSize().width>sPTextImageDirectory.getSize().width-5){
                sPTextImageDirectory.setSize(sPTextImageDirectory.getWidth(),textInstrSelectedPhoto.getHeight()+15);
                //System.out.println(true); //debug
            }else{
                sPTextImageDirectory.setSize(sPTextImageDirectory.getWidth(),textInstrSelectedPhoto.getHeight());
                //System.out.println(false); //debug
            }

        }

        //termino programma
        if(source==menuItemExit){
            System.exit(0);
            //textImageDirectory.setText(""); //debug
        }

        //converto file
        if(source==buttonConvert){
            //se ho selezionato un formato di conversione e un file da convertire
            if(bGroupConversionFormate.getSelection()!=null&&bGroupCharPalette.getSelection()!=null&&converter.getImage()!=null){

                if(checkBoxInverted.isSelected()){
                    converter.setInverted(true);
                }else{
                    converter.setInverted(false);
                }

                //se ho selezionato palette custom prendo manualmente il testo che ho messo
                if(rButtonsCharPalette[rButtonsCharPalette.length-1].isSelected()){
                    converter.setPalette(textCustomPalette.getText());
                }

                folderChooser.showOpenDialog(null);//apre la finestra e aspetta per risultato
                conversionFile=folderChooser.getSelectedFile();

                //se ho effettivamente scelto un file di destinazione e il file non esiste
                if(folderChooser.getSelectedFile()!=null&&!conversionFile.exists()){

                    //sistemo estensione file
                    if(rButtonsConversionFormate[0].isSelected()){
                        if(conversionFile.getName().contains(".")){
                            if(!conversionFile.getName().contains(".txt")){
                                conversionFile = new File(conversionFile.getPath().substring(0,conversionFile.getPath().lastIndexOf('.')) + ".txt");
                            }
                        }else{
                            conversionFile = new File(conversionFile.getPath() + ".txt");
                        }
                    }
                    if(rButtonsConversionFormate[1].isSelected() || rButtonsConversionFormate[2].isSelected() || rButtonsConversionFormate[3].isSelected()){
                        if(conversionFile.getName().contains(".")){
                            if(!conversionFile.getName().contains(".png")){
                                conversionFile = new File(conversionFile.getPath().substring(0,conversionFile.getPath().lastIndexOf('.')) + ".png");
                            }
                        }else{
                            conversionFile = new File(conversionFile.getPath() + ".png");
                        }
                    }

                    //creo il file di destinazione di elaborazione
                    try {

                        conversionFile.createNewFile();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                    //eseguo elaborazioni su buffered image
                    asciiMatrix = converter.convert();

                    //trasformo matrice in singola stringa
                    StringBuilder asciiString= new StringBuilder("");
                    for(int i=0;i<asciiMatrix[0].length;i++){
                        for(int j=0;j<asciiMatrix.length;j++){
                            asciiString.append(asciiMatrix[j][i]);
                        }
                        asciiString.append("\n");
                    }
                    //s.append("\n\n\n");


                    //System.out.println(s); //debug

                    //creo scrittore file
                    PrintWriter writer;
                    try {
                        writer=new PrintWriter(new FileOutputStream(conversionFile.getPath(),true));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    //System.out.println("path finale: " + conversionFile.getPath()); //debug


                    if(rButtonsConversionFormate[0].isSelected()){
                        //scrivo su file
                        writer.write(asciiString.toString());
                    }//conversione txt
                    if(rButtonsConversionFormate[1].isSelected()){
                        //System.out.println("conversione bmp"); //debug

                        int scale = 1;
                        BufferedImage imageOutput = new BufferedImage(asciiMatrix.length * MonospaceWriter.letterWight * scale, asciiMatrix[0].length * MonospaceWriter.letterHeight * scale, BufferedImage.TYPE_INT_RGB);

                        //disegno caratteri su imageOutput
                        for(int x = 0; x < asciiMatrix.length; x++){
                            for(int y = 0; y < asciiMatrix[0].length; y++){
                                MonospaceWriter.write(imageOutput,asciiMatrix[x][y],x*scale*MonospaceWriter.letterWight, y*scale*MonospaceWriter.letterHeight,scale,Color.BLACK,Color.WHITE);
                                //System.out.println(x + "   " + y); //debug
                            }
                        }



                        //this.getGraphics().drawImage(imageOutput,0,0,null); debug (disegno immagine su schermata)


                        //"scrivo" immagine sul file
                        try {
                            ImageIO.write(imageOutput, "png", conversionFile);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }

                    }//conversione bmp gray scale
                    if(rButtonsConversionFormate[2].isSelected()){
                        //System.out.println("conversione bmp color"); //debug

                        Color[][] colors = converter.convertColor();

                        int scale = 1;
                        BufferedImage imageOutput = new BufferedImage(asciiMatrix.length * MonospaceWriter.letterWight * scale, asciiMatrix[0].length * MonospaceWriter.letterHeight * scale, BufferedImage.TYPE_INT_RGB);

                        //disegno caratteri su imageOutput
                        for(int x = 0; x < asciiMatrix.length; x++){
                            for(int y = 0; y < asciiMatrix[0].length; y++){
                                MonospaceWriter.write(imageOutput,asciiMatrix[x][y],x*scale*MonospaceWriter.letterWight, y*scale*MonospaceWriter.letterHeight,scale,colors[x][y],Color.WHITE);
                                //System.out.println(x + "   " + y); //debug
                            }
                        }



                        //this.getGraphics().drawImage(imageOutput,0,0,null); debug (disegno immagine su schermata)


                        //"scrivo" immagine sul file
                        try {
                            ImageIO.write(imageOutput, "png", conversionFile);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }

                    }//conversione bmp color 1
                    if(rButtonsConversionFormate[3].isSelected()){
                        //System.out.println("conversione bmp color"); //debug

                        Color[][] colors = converter.convertColor();

                        int scale = 1;
                        BufferedImage imageOutput = new BufferedImage(asciiMatrix.length * MonospaceWriter.letterWight * scale, asciiMatrix[0].length * MonospaceWriter.letterHeight * scale, BufferedImage.TYPE_INT_RGB);

                        //disegno caratteri su imageOutput
                        for(int x = 0; x < asciiMatrix.length; x++){
                            for(int y = 0; y < asciiMatrix[0].length; y++){
                                MonospaceWriter.write(imageOutput,asciiMatrix[x][y],x*scale*MonospaceWriter.letterWight, y*scale*MonospaceWriter.letterHeight,scale,Color.black,colors[x][y]);
                                //System.out.println(x + "   " + y); //debug
                            }
                        }



                        //this.getGraphics().drawImage(imageOutput,0,0,null); debug (disegno immagine su schermata)


                        //"scrivo" immagine sul file
                        try {
                            ImageIO.write(imageOutput, "png", conversionFile);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }

                    }//conversione bmp color 2


                    //chiudo rile
                    writer.flush();
                    writer.close();
                    //System.out.println("Finito"); // debug
                }else{
                    //System.out.println("errore di: \"se ho effettivamente scelto un file di destinazione e il file non esiste\"");
                }
            }

        }

        //aumento rateo conversione
        if(source==buttonMoreRateo){
            converter.setConversionRateo(converter.getConversionRateo()+1);
            updateResolutionInfo();
        }

        //diminuisco rateo conversione
        if(source==buttonLessRateo){
            converter.setConversionRateo(converter.getConversionRateo()-1);
            updateResolutionInfo();
        }

        //controllo bottoni palette
        for(int i=0;i<rButtonsCharPalette.length;i++){
            if(source==rButtonsCharPalette[i]){
                if(i==rButtonsCharPalette.length-1){//se ho selezionato palette custom
                    sPTextCustomPalette.setVisible(true);
                }else{
                    converter.setPalette(AsciiConverter.PALETTES[i]);
                    sPTextCustomPalette.setVisible(false);
                }
            }
        }




    }//fine: action performed(ActionEvent e)



    public void run() {

        while(true){
        //se c'e bisogno di barra scorrimento aumento altezza
        if(textCustomPalette.getPreferredSize().width>sPTextCustomPalette.getSize().width-5){
            sPTextCustomPalette.setSize(sPTextCustomPalette.getWidth(),textInstrSelectPalette.getHeight()+3+15);
            //System.out.println(true); //debug
        }else{
            sPTextCustomPalette.setSize(sPTextCustomPalette.getWidth(),textInstrSelectPalette.getHeight()+3);
            //System.out.println(false); //debug
        }


        //sleep
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        }
    }
}








