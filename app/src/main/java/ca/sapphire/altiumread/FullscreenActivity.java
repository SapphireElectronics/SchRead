package ca.sapphire.altiumread;

import ca.sapphire.altiumread.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;




/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class FullscreenActivity extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        viewFile();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }


    /**
     * New Code
     */

    int[][]	junctions = new int[1024][3];

    private ArrayList<Integer[]> lines = new ArrayList< Integer[] >();
    int[] colours = new int[50];


//    private Frame mainFrame;
//    private Label headerLabel;
//    private Label statusLabel;
//    private Panel controlPanel;

    private List<Map<String, Object>> records;

    int wire = 0;
    int junction = 0;

    /**
     * Record Types
     *
     * Num	Imp	Name
     * 1  	N	Component ??
     * 2  	N	Pin
     * 6  	Y	Component lines
     * 14 	N
     * 25	N   Net label
     * 26	Y	Bus - multi segment line: LOCATIONCOUNT segments, X1,Y1 , X2,Y2 etc.  TODO bus width
     * 27 	Y	Wire
     * 29 	Y	Junction
     * 34	N	Text drawn with Font
     * 37	Y	Bus entry ("LOCATION.X|Y" is the Bus end of the entry, CORNER.X|Y is the Wire end
     * 41 	N	Text - pin name, or more generally an attribute?
     * 44 	N	?? (single field)
     * 45 	N	Component description?
     * 46	N	?? (single field)
     * 48	N	?? (single field)
     */



    public void viewFile() {
        CompoundFile file = new CompoundFile( "/sdcard/Download/gclk.SchDoc");

    }
}

/**/

//
//public class AltiumRead {
//    //	int[][] wires = new int[4096][5];
//    int[][]	junctions = new int[1024][3];
//
//    private ArrayList<Integer[]> lines = new ArrayList< Integer[] >();
//    int[] colours = new int[50];
//
//
//    private Frame mainFrame;
//    private Label headerLabel;
//    private Label statusLabel;
//    private Panel controlPanel;
//
//    private List<Map<String, Object>> records;
//
//    int wire = 0;
//    int junction = 0;
//
//    /**
//     * Record Types
//     *
//     * Num	Imp	Name
//     * 1  	N	Component ??
//     * 2  	N	Pin
//     * 6  	Y	Component lines
//     * 14 	N
//     * 25	N   Net label
//     * 26	Y	Bus - multi segment line: LOCATIONCOUNT segments, X1,Y1 , X2,Y2 etc.  TODO bus width
//     * 27 	Y	Wire
//     * 29 	Y	Junction
//     * 34	N	Text drawn with Font
//     * 37	Y	Bus entry ("LOCATION.X|Y" is the Bus end of the entry, CORNER.X|Y is the Wire end
//     * 41 	N	Text - pin name, or more generally an attribute?
//     * 44 	N	?? (single field)
//     * 45 	N	Component description?
//     * 46	N	?? (single field)
//     * 48	N	?? (single field)
//     */
//
//
//    public AltiumRead( String fileName ){
//
//        try {
//
//                ParametricFile parametricFile = new ParametricFile();
//                POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(new File(fileName)));
//
//                if (parametricFile.load(new DocumentInputStream((DocumentEntry) fs.getRoot().getEntry("FileHeader")))) {
//                    System.out.println("Successful!");
//
//                    records = parametricFile.getRecords();
//
//                    System.out.println( "Records : " + records.size());
//
////					for( int i=0; i<records.size(); i++ ) {
////						System.out.println( new JSONObject(records.get(i)) );
////					}
//
////					System.out.println( "Record 0 : " + new JSONObject(records.get(0)) );
////					System.out.println( "Record 0 : " + records.get(0) );
////					System.out.println( "Length :" + records.get(0).size() );
////					System.out.println( "Number :" + records.get(0).get( "RECORD" ) );
//
//                    for (Map<String, Object> record : records) {
//                        System.out.println( "Record (" + record.get( "RECORD" ) + ") : " + record );
//
//                        int recordType = Integer.parseInt( (String) record.get( "RECORD" ) );
//
//                        // bus
//                        if( recordType == 26 ) {
//                            int size = Integer.parseInt( (String) record.get( "LOCATIONCOUNT" ) );
//                            int[][] points = new int[size][2];
//
//                            for( int i=0; i<size; i++ ) {
//                                String xPoint = "X" + String.valueOf(i+1);
//                                String yPoint = "Y" + String.valueOf(i+1);
//
//                                points[i][0] = Integer.parseInt( (String) record.get( xPoint ) );
//                                points[i][1] = Integer.parseInt( (String) record.get( yPoint ) );
//                            }
//
//                            for( int i=0; i<size-1; i++ ) {
//                                Integer[] line = new Integer[6];
//                                line[0] = points[i][0];
//                                line[1] = points[i][1];
//                                line[2] = points[i+1][0];
//                                line[3] = points[i+1][1];
//                                line[4] = javaColour(Integer.parseInt( (String) record.get( "COLOR") ));
//                                line[5] = 3;
//                                lines.add( line );
//                            }
//                        }
//
//
//                        // wire
//                        if( recordType == 27 ) {
//                            Integer[] line = new Integer[6];
//                            line[0] = Integer.parseInt( (String) record.get( "X1") );
//                            line[1] = Integer.parseInt( (String) record.get( "Y1") );
//                            line[2] = Integer.parseInt( (String) record.get( "X2") );
//                            line[3] = Integer.parseInt( (String) record.get( "Y2") );
//                            line[4] = javaColour(Integer.parseInt( (String) record.get( "COLOR") ));
//                            line[5] = 1;
//                            lines.add( line );
//                        }
//
//                        // bus entry
//                        if( recordType == 37 ) {
//                            Integer[] line = new Integer[6];
//                            line[0] = Integer.parseInt( (String) record.get( "LOCATION.X") );
//                            line[1] = Integer.parseInt( (String) record.get( "LOCATION.Y") );
//                            line[2] = Integer.parseInt( (String) record.get( "CORNER.X") );
//                            line[3] = Integer.parseInt( (String) record.get( "CORNER.Y") );
//                            line[4] = javaColour(Integer.parseInt( (String) record.get( "COLOR") ));
//                            line[5] = 1;
//                            lines.add( line );
//                        }
//
//                        // component lines
//                        if( recordType == 6 ) {
//                            int size = Integer.parseInt( (String) record.get( "LOCATIONCOUNT" ) );
//                            int[][] points = new int[size][2];
//
//                            for( int i=0; i<size; i++ ) {
//                                String xPoint = "X" + String.valueOf(i+1);
//                                String yPoint = "Y" + String.valueOf(i+1);
//
//                                points[i][0] = Integer.parseInt( (String) record.get( xPoint ) );
//                                points[i][1] = Integer.parseInt( (String) record.get( yPoint ) );
//                            }
//
//                            for( int i=0; i<size-1; i++ ) {
//                                Integer[] line = new Integer[6];
//                                line[0] = points[i][0];
//                                line[1] = points[i][1];
//                                line[2] = points[i+1][0];
//                                line[3] = points[i+1][1];
//                                line[4] = javaColour(Integer.parseInt( (String) record.get( "COLOR") ));
//                                line[5] = 1;
//                                lines.add( line );
//                            }
//                        }
//
//                        // junction??
//                        if( recordType == 29 ) {
//                            int color = Integer.parseInt( (String) record.get( "COLOR") );
//                            junctions[junction][0] = Integer.parseInt( (String) record.get( "LOCATION.X") );
//                            junctions[junction][1] = Integer.parseInt( (String) record.get( "LOCATION.Y") );
//                            junctions[junction][2] = color;
//                            junction++;
//                        }
//                    }
//
//                    prepareGUI();
//                    showImageDemo();
//                }
//        }
//        catch( IOException e ) {
//            e.printStackTrace();
//        }
//    }
//
//    // Altium colours are stored BGR
//    // Java colours are stored RGB
//    public int javaColour( int altiumColour )
//    {
//        int javaColour = (altiumColour & 0xff) << 16;
//        javaColour |= altiumColour & 0xff00;
//        javaColour |= (altiumColour & 0xff0000) >> 16;
//        return javaColour;
//    }
//
//    public int getInt( Map<String, Object> record, String element ) {
//        return  Integer.valueOf( (String) record.get(  element ) );
//    }
//
//
//    public static void main(String[] args) throws IOException {
//        if (args.length==0 || args.length>2) {
//            System.out.println("Please specify valid file");
//            return;
//        }
//
//        List<String> argsList = Arrays.asList(args);
//        if (!argsList.contains("-c")) {
//            new AltiumRead( args[0], false );
////			unpackFile(new File(args[0]));
//        } else {
//            new AltiumRead( args[1], true );
////			ParametricFile parametricFile = new ParametricFile();
////			parametricFile.setWires(wires);
////			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(new File(args[args.length - 1])));
////			if (parametricFile.load(new DocumentInputStream((DocumentEntry) fs.getRoot().getEntry("FileHeader")))) {
////				System.out.println("Successful!");
////
////				records = parametricFile.getRecords();
////
////				for( Map<String, Object> record : records ) {
////					System.out.println( "**********" );
////					for( Map.Entry<String, Object> field : record.entrySet() ) {
////						System.out.println( field );
////						System.out.println( "==========" );
////
////					}
////				}
//            //		        System.out.println(parametricFile.getJSON());
//            //		        System.out.println(parametricFile.getJSON().toString());
//        }
//    }
//
//
//
//    private void unpackFile(File file) throws IOException {
//        POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
//        System.out.println( file.getName() + ".export");
//        //		    exportDirEntry(fs.getRoot(), new File(file.getParentFile(), file.getName() + ".export"));
//        exportDirEntry(fs.getRoot(), new File(file.getParentFile(), "test.export"));
//    }
//
//    private void exportDirEntry(DirectoryEntry dirEntry, File dir) throws IOException {
////		System.out.println( dirEntry + "  :  " + dir );
//
//        if( !dir.exists() )
//            if (!dir.mkdirs()) throw new IOException("Can't create dir");
//
//        for (Entry entry : dirEntry) {
//            if (entry.isDirectoryEntry()) {
//                exportDirEntry((DirectoryEntry) entry, new File(dir, entry.getName()));
//            } else if (entry.isDocumentEntry()) {
//                exportFileEntry(entry, dir);
//            }
//        }
//    }
//
//    private void exportFileEntry(Entry entry, File dir) throws IOException {
//        OutputStream output = new FileOutputStream(new File(dir, entry.getName()));
//        InputStream input = new DocumentInputStream((DocumentEntry) entry);
//
//        byte[] buffer = new byte[4096]; // Adjust if you want
//        int bytesRead;
//        while ((bytesRead = input.read(buffer)) != -1)
//            output.write(buffer, 0, bytesRead);
//
//        input.close();
//        output.close();
//    }
//
//
//
//
//    private void prepareGUI(){
//        mainFrame = new Frame("Altium Reader");
//        mainFrame.setSize(1200,800);
//        mainFrame.setLayout(new GridLayout(1, 1));
//        mainFrame.addWindowListener(new WindowAdapter() {
//            public void windowClosing(WindowEvent windowEvent){
//                System.exit(0);
//            }
//        });
//        headerLabel = new Label();
//        headerLabel.setAlignment(Label.CENTER);
//        statusLabel = new Label();
//        statusLabel.setAlignment(Label.CENTER);
//        statusLabel.setSize(350,100);
//
//        controlPanel = new Panel();
//        controlPanel.setLayout(new FlowLayout());
//
//        //			      mainFrame.add(headerLabel);
//        //			      mainFrame.add(controlPanel);
//        //			      mainFrame.add(statusLabel);
//        mainFrame.setVisible(true);
//    }
//
//    private void showImageDemo(){
//        headerLabel.setText("Control in action: Image");
//
//        mainFrame.add(new ImageComponent());
//        //			      controlPanel.add(new ImageComponent());
//        //			      controlPanel.add(new ImageComponent("resources/java.jpg"));
//        mainFrame.setVisible(true);
//    }
//
//    class ImageComponent extends Component {
//
//        public void paint(Graphics g) {
////			g.setColor( Color.RED);
////			g.drawLine( 100, 200, 200, 100 );
////			g.drawLine( 100, 100, 200, 200 );
////			g.drawLine(50,  50,  250,  50 );
////			g.drawLine(250,  50,  250,  250);
////			g.drawLine(250,  250,  50,  250);
////			g.drawLine(50,  250,  50,  50);
//
////			g.setColor( new Color( javaColour( wires[0][4] ) ) );
////			for( int i=0; i<wire; i++ ) {
////				g.drawLine( wires[i][0],  wires[i][1], wires[i][2], wires[i][3]);
////			}
//
//            Graphics2D g2 = (Graphics2D) g;
//            g2.scale( 1, -1 );
//            g2.translate( 0, -800 );
//
//
//            g2.setColor( new Color( javaColour( junctions[0][2] ) ) );
//            for( int i=0; i<junction; i++ ) {
//                int x = junctions[i][0];
//                int y = junctions[i][1];
//                g2.drawOval(x-2, y-2, 4, 4);
//            }
//
//            for (Integer[] line : lines) {
//                g2.setColor( new Color( line[4] ) );
//                if( line[5] > 1 )
//                    g2.setStroke( new BasicStroke(3) );
//                else
//                    g2.setStroke( new BasicStroke(1) );
//
//                g2.drawLine( line[0], line[1], line[2], line[3] );
//            }
//        }
//
//
//        public Dimension getPreferredSize() {
//            return new Dimension( 1200, 800);
//            //			         if (img == null) {
//            //			            return new Dimension(100,100);
//            //			         } else {
//            //			            return new Dimension(img.getWidth(), img.getHeight());
//            //			         }
//        }
//    }
//}
