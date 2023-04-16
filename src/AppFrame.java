import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class AppFrame extends JFrame implements ActionListener{
    //palette
    private static final Color colorBackGround=new Color(222, 222, 222);
    private static final Color colorTextImageDirectory=new Color(107, 156, 190);
    private static final Color colorButtonConvert=new Color(26, 208, 39);

    //campi statici
    private static final String[] supportedConversionFormats={"txt","exe","bmt"};


    //campi non statici
    private static final int frameWidth=800,frameHeight=600;

    private final Container c;
    private JMenuBar menuBar;
    private JMenu menuFile;
    private JMenuItem menuItemSelectPhoto,menuItemExit;
    private JScrollPane sPTextImageDirectory;
    private JTextArea textImageDirectory,textInstrSelectedPhoto,textResolutions;
    private JTextArea textInstrConversionRateo1,textInstrConversionRateo2,textInstrConversionRateo3;
    JButton buttonImageDirectory;//invisibile e sovrapposto con scritta directory
    private JRadioButton[] rButtonsConversionFormate;
    private ButtonGroup bGroupConversionFormate;
    private JFileChooser fileChooser,folderChooser;
    private JButton buttonConvert,buttonMoreRateo,buttonLessRateo;

    private File selectedImage,conversionFile;
   private AsciiConverter converter;
   private char[][] asciiMatrix;


    //costruttore
    AppFrame(){
        super("v1 - convertitore ascii art");
        c=getContentPane();
        c.setLayout(null);
        c.setBackground(colorBackGround);
        //c.setBackground(Color.black); //debug
        selectedImage=null;

        converter=new AsciiConverter();
        asciiMatrix=null;


        buildMenuBar();
        buildDirectoriesTexts();
        buildRButtonsConversionFormate();
        buildResolutionIndicators();
        buildScrollPanels();
        buildButtons();

        buildFileChooser();
        buildFolderChooser();

        addComponents();

        //compilo testi (e posiziono componenti)
        updateResolutionInfo();


        setBounds((getToolkit().getScreenSize().width-frameWidth)/2,(getToolkit().getScreenSize().height-frameHeight)/2,frameWidth,frameHeight);
        setVisible(true);
        setResizable(false);
        setIconImage(new ImageIcon("images/compratore.png").getImage());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
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
    private void buildScrollPanels(){
        //barra scorrimento per testo directory immagine scelta
        sPTextImageDirectory=new JScrollPane(textImageDirectory);
        sPTextImageDirectory.setBounds(textInstrSelectedPhoto.getX()+textInstrSelectedPhoto.getWidth(),textInstrSelectedPhoto.getY(),260,textInstrSelectedPhoto.getHeight());

    }
    private void buildRButtonsConversionFormate(){
        bGroupConversionFormate=new ButtonGroup();

        rButtonsConversionFormate=new JRadioButton[supportedConversionFormats.length];

        JRadioButton curButton;
        int buttonsHeight=135;
        for(int i=0;i<rButtonsConversionFormate.length;i++){
            curButton=new JRadioButton(supportedConversionFormats[i]);

            curButton.setSize(curButton.getPreferredSize());
            if(i==0){
                curButton.setLocation(10,buttonsHeight);
            }else{
                curButton.setLocation(rButtonsConversionFormate[i-1].getX()+rButtonsConversionFormate[i-1].getWidth()+10,buttonsHeight);
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
        buttonConvert.setBounds(40,400,200,40);
        buttonConvert.setHorizontalTextPosition(SwingConstants.CENTER);
        buttonConvert.setVerticalTextPosition(SwingConstants.CENTER);
        buttonConvert.setBackground(colorButtonConvert);
        buttonConvert.setFocusable(false);
        buttonConvert.addActionListener(this);
    }
    private void addComponents(){
        c.add(buttonImageDirectory);//importante che stia sopra per avere priorità
        setJMenuBar(menuBar);
        c.add(textInstrSelectedPhoto);
        c.add(sPTextImageDirectory);
        for(int i=0;i<rButtonsConversionFormate.length;i++){
            c.add(rButtonsConversionFormate[i]);
        }
        c.add(buttonConvert);
        c.add(textResolutions);
        c.add(textInstrConversionRateo1);
        c.add(textInstrConversionRateo2);
        c.add(textInstrConversionRateo3);
        c.add(buttonMoreRateo);
        c.add(buttonLessRateo);
    }
    private void buildResolutionIndicators(){
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
        textResolutions.setLocation(10,180);
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
            /*
            if(bGroupConversionFormate.getSelection()!=null&&converter.getImage()!=null&&converter.getPalette()!=null){

                folderChooser.showOpenDialog(null);//apre la finestra e aspetta per risultato

                //se ho effettivamente scelto un file di destinazione e il file non esiste
                if(folderChooser.getSelectedFile()!=null&&!conversionFile.exists()){
                    //creo il file di destinazione di elaborazione
                    conversionFile=folderChooser.getSelectedFile();
                    try {
                        conversionFile.createNewFile();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                    //eseguo elaborazioni su buffered image
                    asciiMatrix=converter.convert();

                    System.out.println(asciiMatrix);

                    this.getGraphics().drawImage(converter.getImage(),300,300,null);

                }
            }
             */

            converter.setPalette(AsciiConverter.PALETTE_15_MIXED);
            converter.convert();
            this.getGraphics().drawImage(converter.getImage(),300,300,null);
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

    }//fine: action performed(ActionEvent e)

}








