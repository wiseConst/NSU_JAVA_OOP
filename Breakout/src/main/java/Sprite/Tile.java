package Sprite;

import java.awt.*;
import java.awt.image.BufferedImage;


public class Tile extends Sprite {
    
    private boolean m_bIsDestroyed = false;

    public Tile(BufferedImage texture, int x, int y) {
        m_Texture = texture;
        m_X = x;
        m_Y = y;
    }

    public boolean IsDestroyed() {
        return m_bIsDestroyed;
    }

    public void SetDestroyed(boolean isDestroyed) {
        m_bIsDestroyed = isDestroyed;
    }


}
