/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.looi.looi.gui_essentials;

import com.looi.looi.MiscellaneousMethods;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 *
 * @author peter_000
 */
public class TextBox extends Rectangle
{
    public static final double DEFAULT_CURSOR_WIDTH = 3;
    public static final double DEFAULT_SPAM_DELAY = .2;//in seconds
    public static final Color DEFAULT_TEXT_COLOR = Color.BLACK;
    public static final double DEFAULT_HORIZONTAL_MARGIN = 5;
    public static final double DEFAULT_VERTICAL_MARGIN = 5;
    public static final double DEFAULT_LINE_SPACING = 3;
    public static final Color DEFAULT_DEFAULT_TEXT_COLOR = Color.LIGHT_GRAY;
    public static final char[] DEFAULT_VALID_CHARACTERS = {' ','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','.',',','<','>','/','?',':',';','\"','\'','{','}','[',']','|','\\','-','_','+','=','1','2','3','4','5','6','7','8','9','0','`','~','!','@','#','$','%','^','&','*','(',')'};
    public static final Background DEFAULT_BACKGROUND = Background.WHITE_BACKGROUND;
    public static final Font DEFAULT_FONT = Font._16_PT_PLAIN;
    
    private boolean editable;
    private String defaultText;
    private String text;
    private Font font = DEFAULT_FONT;
    private Color textColor = DEFAULT_TEXT_COLOR;
    private double verticalMargin = DEFAULT_VERTICAL_MARGIN;
    private double horizontalMargin = DEFAULT_HORIZONTAL_MARGIN;
    private double lineSpacing = DEFAULT_LINE_SPACING;
    private double spamDelay = DEFAULT_SPAM_DELAY;
    private double spamTimer = 0;
    private int currentKey;
    
    private ArrayList<String> lines = new ArrayList<>();
    
    //private int row;
    //private int indexInLine; //cursor is between indexInLine and indexInLine+1
    private int cursorIndex = 0;
    private boolean selected = false;
    private Cursor cursor;
    private double cursorWidth = DEFAULT_CURSOR_WIDTH;
    private boolean lastMouseClickValid = false;
    
    private Graphics graphics;
    private Color defaultTextColor = DEFAULT_DEFAULT_TEXT_COLOR;
    private boolean resetDisplayRequired = false;
    
    private int[] lowercaseCharacterWidthsAToZ = new int[26];
    private int[] uppercaseCharacterWidthsAToZ = new int[26];
    private int spaceCharacterWidth;
    private char[] validCharacters = DEFAULT_VALID_CHARACTERS;
    
    public TextBox(double x, double y, double width, double height, String text, boolean editable)
    {
        super(x,y,width,height,DEFAULT_BACKGROUND);
        setDefaultText(text);
        setEditable(editable);
        setText(text);
        cursor = newCursor();
        cursor.setLayer(getLayer() - 1);
        add(cursor);
        cursor.deactivate();
    }
    
    public void activate(Object... activationInfo)
    {
        super.activate(activationInfo);
        if(!isSelected())
        {
            cursor.deactivate(activationInfo);
        }
    }
    public void deactivate(Object... activationInfo)
    {
        super.deactivate(activationInfo);
    }
    public int getCursorIndex()
    {
        return cursorIndex;
    }
    public synchronized void setText(String text)
    {
        TextBox.this.text = text;
        
        if(graphics == null)
        {
            resetDisplayRequired = true;
            return;
        }
        Font scaledFont = new Font(getFont().getName(),getFont().getStyle(),scaleW(getFont().getSize()));

        /*int indexOfLastSimilarity = 0;
        for(int i = 0; i < Math.min(this.text.length(),text.length()); i++)
        {
            if(this.text.charAt(i) == text.charAt(i))
            {
                indexOfLastSimilarity = i;
            }
            else
            {
                break;
            }
        }*/
        
        
        //double minY = getY() + getVerticalMargin();? used or not
        //double maxY = getY() + getHeight() - getVerticalMargin();? same as above
        double minX = getX() + getHorizontalMargin();
        double maxX = getX() + getWidth() - getHorizontalMargin();
        
        StringBuffer restOfText;
        
        /*if(indexOfLastSimilarity > 0)
        {
            if(getText().length() > indexOfLastSimilarity + 1)
            {
                String rest = "";
                
                while(lines.size() > getRow())
                {
                    rest = lines.remove(lines.size() - 1) + rest;
                }
                restOfText = new StringBuffer(getText().substring(indexOfLastSimilarity + 1));
            }
            else
            {
                restOfText = new StringBuffer("");
            }
            
        }
        else
        {*/
            lines.clear(); 
            restOfText = new StringBuffer(getText());
        //}
        
        


        
        String row = "";

        while(restOfText.length() > 0)
        {
            int nextSpace = restOfText.indexOf(" ");
            String nextWord;
            if(nextSpace != -1)// if there is a next space at all, 
            {
                nextWord = restOfText.substring(0,nextSpace+1);//gets the word and the next space, and tries to see if it will fit in one line with the row as well
                //restOfText = restOfText.substring(nextSpace+1);
                restOfText.delete(0,nextSpace+1);
            } 
            else//if there is no space
            {
                //nextWord = restOfText;
                //restOfText = "";
                nextWord = restOfText.toString();
                restOfText.delete(0,restOfText.length());
            }

            if(stringWidth(row + nextWord,scaledFont) <= scaleW(maxX - minX))
            {
                row += nextWord;//yes, it fits in one line. Add it, and then get rid of it from the rest of the text
            }
            else//No. It goes over. 
            {
                if(stringWidth(nextWord,scaledFont) > scaleW(maxX - minX))//if the next word alone, when pushed down, will still be greater than the length
                {
                    String greatestPossibleLine = row + nextWord;// find the greatest possible text for this line

                    for(int index = greatestPossibleLine.length(); index >= 0; index--)
                    {
                        String partToTest = greatestPossibleLine.substring(0,index);
                        if(stringWidth(partToTest,scaledFont) <= scaleW(maxX - minX))//find where the word can break
                        {
                            row = partToTest;//chop it off
                            lines.add(row);
                            row = "";
                            String remainderOfGreatesPossibleLine = greatestPossibleLine.substring(partToTest.length());
                            //restOfText = remainderOfGreatesPossibleLine + restOfText;
                            restOfText.insert(0,remainderOfGreatesPossibleLine);
                            break;
                        }
                    }
                }
                else //if the next word can indeed be pushed down
                {
                    lines.add(row);
                    row = "";
                    //restOfText = nextWord + restOfText;
                    restOfText.insert(0,nextWord);
                }
            }
            
        }
        lines.add(row);//dont forget to add the last row, as rows are only added when stuff goes over the line
    }
    
    protected int stringWidth(String s, Font scaledFont)
    {
        graphics.setFont(scaledFont);
        //return graphics.getFontMetrics().stringWidth(s);
        int width = 0;
        for(int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            if(c >= 65 && c <= 90)
            {
                width += this.uppercaseCharacterWidthsAToZ[c-65];
            }
            else if(c >= 97 && c <= 122)
            {
                width += this.lowercaseCharacterWidthsAToZ[c-97];
            }
            else if(c == ' ')
            {
                width += this.spaceCharacterWidth;
            }
            else
            {
                width += graphics.getFontMetrics().stringWidth(c+"");
            }
        }
        return width;
    }
    public String getText(){return text;}
    public void setDefaultText(String defaultText){this.defaultText = defaultText;}
    public String getDefaultText(){return defaultText;}
    public void setFont(Font f)
    {
        font = f;
        if(graphics == null)
        {
            return;
        }
        
        Font scaledFont = new Font(getFont().getName(),getFont().getStyle(),scaleW(getFont().getSize()));
        
        Graphics g = graphics.create();
        g.setFont(scaledFont); 
        for(int i = 0; i < 26; i++)
        {
            uppercaseCharacterWidthsAToZ[i] = g.getFontMetrics().stringWidth(((char)(i+65))+"");
        }
        for(int i = 0; i < 26; i++)
        {
            lowercaseCharacterWidthsAToZ[i] = g.getFontMetrics().stringWidth(((char)(i+97))+"");
        }
        spaceCharacterWidth = g.getFontMetrics().stringWidth(" ");
    }
    public Font getFont(){return font;}
    public Color getTextColor(){return textColor;}
    public void setTextColor(Color textColor){this.textColor = textColor;}
    public boolean isEditable(){return editable;}
    public void setEditable(boolean editable){this.editable = editable;}
    
    public double getVerticalMargin(){return verticalMargin;}
    public double getHorizontalMargin(){return horizontalMargin;}
    public void setVerticalMargin(double d){verticalMargin = d;}
    public void setHorizontalMargin(double d){horizontalMargin = d;}
    public void setLineSpacing(double d){lineSpacing = d;}
    public double getLineSpacing(){return lineSpacing;}
    public double getCursorWidth(){return cursorWidth;}
    public void setCursorWidth(double d){this.cursorWidth = d;}
    public void setSpamDelay(double d){spamDelay = d;}
    public double getSpamDelay(){return spamDelay;}
    public Color getDefaultTextColor(){return defaultTextColor;}
    public void setDefaultTextColor(Color c){defaultTextColor = c;}
    public char[] getValidCharacters(){return validCharacters;}
    public void setValidCharacters(char[] validCharacters){this.validCharacters = validCharacters;}
    public boolean isSelected(){return selected;}
    public Cursor getCursor(){return cursor;}
    protected void savePaintInformation()
    {
        
        
    }
    protected void looiPaint()
    {
        super.setFont(new Font("",Font.PLAIN,50));
        boolean empty = getText().length() == 0;
        if(empty)
        {
            setText(getDefaultText());
        }
        super.looiPaint();
        this.graphics = getGraphics().create();
        if(empty)
        {
            setColor(getDefaultTextColor());
        }
        else
        {
            setColor(getTextColor());
        }
        
        synchronized(this)//so that looistep doesn't also edit it at the same time
        {
            for(int i = 0; i < lines.size(); i++)
            {
                super.setFont(getFont());//MUST be SUPER.setFont(...) because setFont(...) is overridden
                double offsetDueToNotBeingTheFirstLine = i * (getFont().getSize() + getLineSpacing());

                if(!(offsetDueToNotBeingTheFirstLine + getY() + getFont().getSize() + getVerticalMargin() > getY() + getHeight() - getVerticalMargin()))
                {
                    drawString(lines.get(i),getX() + getHorizontalMargin(),offsetDueToNotBeingTheFirstLine + getY() + getFont().getSize() + getVerticalMargin());
                }
            }
        }
        if(empty)
        {
            setText("");
        }
    }
    protected void looiStep()
    {
        if(resetDisplayRequired && graphics != null)
        {
            
            setFont(getFont());
            setText(getText());
            resetDisplayRequired = false;
        }
        spamTimer += 1.0/thisWindow().getUPS();
        
        if(isEditable())
        {
            boolean isPressed = false;
            boolean justPressed = false;
            boolean justReleased = false;
            
            boolean mouseOnButton = (getInternalMouseX() > getX() + getHorizontalMargin() && getInternalMouseX() < getX() + getWidth() - getHorizontalMargin() && getInternalMouseY() > getY() + getVerticalMargin()&& getInternalMouseY() < getY() + getHeight() - getVerticalMargin());
            
            //check last click
            if(mouseLeftPressed())
            {
                if(mouseOnButton)
                {
                    lastMouseClickValid = true;
                }
                else
                {
                    lastMouseClickValid = false;
                    deselect();
                }
            }
            if(mouseOnButton)
            {
                if(mouseLeftPressed())
                {
                    justPressed = true;
                }
                if(mouseLeftReleased() && lastMouseClickValid)
                {
                    justReleased = true;
                }
                if(mouseLeftEngaged() && lastMouseClickValid)
                {
                    isPressed = true;
                }
            }
            if(justReleased)
            {
                synchronized(this)
                {
                    whenPressed();
                }
                
            }
        }
    }
    protected String insert(char c, int index, String s)
    {
        
        String first = s.substring(0,index);
        String last = s.substring(index);
        return first + c + last;
    }
    protected String remove(int index, String s)
    {
        if(index < 0)
            return s;
        if(s.length() == 1 && index == 0)
        {
            return "";
        }
        String first = s.substring(0,index);
        String last = s.substring(index);
        if(last.length() > 1)
            last = last.substring(1);
        else
            last = "";
        return first + last;
    }
    protected void keyPressed(KeyEvent e)
    {
        if(!selected)
            return;
        spamTimer = 0;
        currentKey = e.getKeyCode();
        
        if(e.getKeyCode() == KeyEvent.VK_LEFT)
        {
            cursorIndex--;
            if(cursorIndex < 0)
                cursorIndex = 0;
            if(cursorIndex > getText().length())
                cursorIndex = getText().length();
        }
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT)
        {
            cursorIndex++;
            if(cursorIndex < 0)
                cursorIndex = 0;
            if(cursorIndex > getText().length())
                cursorIndex = getText().length();
        }
        
        if(contains(validCharacters,e.getKeyChar()) || e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
        {
            type(e);
        }
    }
    protected void keyEngaged(KeyEvent e)
    {
        if(!selected)
            return;
        if(spamTimer >= spamDelay)
        {
            if(e.getKeyCode() == KeyEvent.VK_LEFT)
            {
                cursorIndex--;
                if(cursorIndex < 0)
                    cursorIndex = 0;
                if(cursorIndex > getText().length())
                    cursorIndex = getText().length();
            }
            else if(e.getKeyCode() == KeyEvent.VK_RIGHT)
            {
                cursorIndex++;
                if(cursorIndex < 0)
                    cursorIndex = 0;
                if(cursorIndex > getText().length())
                    cursorIndex = getText().length();
            }
        }
        
        if((contains(validCharacters,e.getKeyChar()) || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) && e.getKeyCode() == currentKey && spamTimer >= spamDelay)
        {
            type(e);
        }
    }
    protected boolean contains(char[] chars, char c)
    {
        for(char ch : chars)
        {
            if(ch == c)
            {
                return true;
            }
        }
        return false;
    }
    protected synchronized void type(KeyEvent e)
    {
        
        if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
        {
            this.setText(remove(cursorIndex-1,getText()));
            cursorIndex--;
            if(cursorIndex < 0)
                cursorIndex = 0;
        }
        else
        {
            this.setText(insert(e.getKeyChar(),cursorIndex,getText()));
            cursorIndex++;
            
        }
    }
    
    protected synchronized void whenPressed()
    {
        int row;
        int indexInLine = 0;
        row = 0;
        boolean set = false;
        for(int i = 0; i < lines.size(); i++)
        {
            double offsetDueToNotBeingTheFirstLine = i * (getFont().getSize() + getLineSpacing());
            if(getInternalMouseY() < offsetDueToNotBeingTheFirstLine + getY() + getFont().getSize() + getVerticalMargin())
            {
                row = i;
                set = true;
                break;
            }
        }
        if(! set)
        {
            row = lines.size()-1;
            if(row == -1)
            {
                row = 0;
            }
        }
        String line = lines.get(row);
        boolean set2 = false;
        Graphics g = getGraphics().create();
        Font scaledFont = new Font(getFont().getName(),getFont().getStyle(),scaleW(getFont().getSize()));
        g.setFont(scaledFont);
        for(int i = 0; i <= line.length(); i++)
        {
            double centerOfCharacter;
            if(i != 0)
            {
                centerOfCharacter = ( scaleX(getX() + getHorizontalMargin()) + g.getFontMetrics().stringWidth(line.substring(0,i)) + scaleX(getX() + getHorizontalMargin()) + g.getFontMetrics().stringWidth(line.substring(0,i-1)) )/2;
            }
            else
            {
                centerOfCharacter = ( scaleX(getX() + getHorizontalMargin()) + g.getFontMetrics().stringWidth(line.substring(0,i)) + scaleX(getX() + getHorizontalMargin()) + 0 )/2;
            }
            if(scaleX(getInternalMouseX()) < centerOfCharacter)
            {
                indexInLine = i - 1;
                set2 = true;
                break;
            }
        }
        if(! set2)
        {
            indexInLine = line.length();
        }
        int totalIndex = 0;
        for(int i = 0; i < row; i++)
        {
            totalIndex += lines.get(i).length();
        }
        totalIndex += indexInLine;
        cursorIndex = totalIndex;
        select();
    }
    public void select()
    {
        selected = true;
        cursor.activate();
    }
    public void deselect()
    {
        selected = false;
        cursor.deactivate(); 
    }
    public Cursor newCursor()
    {
        return new Cursor();
    }
    protected int getIndexInLine()
    {
        int rowStartIndex = 0;
        String theRow = "";
        for(String s : lines)
        {
            theRow = s;
            rowStartIndex += s.length();
            if(rowStartIndex >= cursorIndex)
            {
                rowStartIndex -= s.length();
                
                break;
            }
        }
        return cursorIndex - rowStartIndex;
        
    }
    protected int getRow()
    {
        int totalIndex = 0;
        for(int r = 0; r < lines.size(); r++)
        {
            totalIndex += lines.get(r).length();
            if(cursorIndex <= totalIndex)
            {
                return r;
            }
        }
        return 0;
    }
    protected void setPaintBoundary(Rectangle r)
    {
        super.setPaintBoundary(r);
        cursor.setPaintBoundary(r);
    }
    
    public class Cursor extends Rectangle
    {
        public static final int DEFAULT_BLINK_TIME = 30;
        private int blinkTime = DEFAULT_BLINK_TIME;
        private int timer = 0;
        private boolean visible = true;
        public Cursor()
        {
            
        }
        protected void looiStep()
        {
            timer++;
            if(timer >= blinkTime)
            {
                toggleVisibility();
                timer = 0;
            }
        }
        protected void looiPaint()
        {
            
            
            super.setDimensions(getCursorWidth(),TextBox.this.getFont().getSize()); 
            synchronized(TextBox.this)
            {
                if(TextBox.this.getGraphics() != null)
                {
                    Font scaledFont = new Font(getFont().getName(),getFont().getStyle(),scaleW(getFont().getSize()));
                    Cursor.this.setFont(scaledFont);
                    double offsetDueToNotBeingTheFirstLine = getRow() * (getFont().getSize() + getLineSpacing());
                    try
                    {
                        double futureX = TextBox.this.getX() + TextBox.this.getHorizontalMargin() + Cursor.this.getGraphics().getFontMetrics().stringWidth(lines.get(getRow()).substring(0,getIndexInLine()))/getViewOverInternalWidth();
                        double futureY = offsetDueToNotBeingTheFirstLine + TextBox.this.getY() + getVerticalMargin();
                        if(futureX != getX() || futureY != getY())
                        {
                            setPosition(futureX,futureY);
                            setVisible(true);
                        }
                        
                    }
                    catch(java.lang.IndexOutOfBoundsException e){}
                }
            }
            if(!visible)
                return;
            super.looiPaint();
        }
        public int getBlinkTime(){return blinkTime;}
        public void setBlinkTime(int i){blinkTime = i;}
        protected void toggleVisibility()
        {
            visible = !visible;
        }
        
        public void setVisible(boolean visible)
        {
            this.visible = visible;
            timer = 0;
        }
        
    }
}

