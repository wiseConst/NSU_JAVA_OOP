package Controller;

import Model.GameModel;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameController implements Controller, KeyListener {

    private GameModel m_ModelRef = null;

    public GameController(GameModel model) {
        m_ModelRef = model;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        m_ModelRef.GetPaddle().keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        m_ModelRef.GetPaddle().keyReleased(e);
    }

    public void update() {
        m_ModelRef.GetPaddle().move();
        m_ModelRef.GetBall().move();

        СheckCollision();
    }

    private void СheckCollision() {
        boolean bAllTilesDestroyed = true;
        for (var tile : m_ModelRef.GetTiles()) {
            if (!tile.IsDestroyed()) {
                bAllTilesDestroyed = false;
                break;
            }
        }

        if (bAllTilesDestroyed) m_ModelRef.SetStopRequested(true);

        final var ball = m_ModelRef.GetBall();
        final var paddle = m_ModelRef.GetPaddle();
        if (ball.getRect().intersects(paddle.getRect())) {
            final int paddleLPos = (int) paddle.getRect().getMinX();
            final int ballLPos = (int) ball.getRect().getMinX();

            final int first = paddleLPos + 8;
            final int second = paddleLPos + 16;
            final int third = paddleLPos + 24;
            final int fourth = paddleLPos + 32;

            if (ballLPos < first) {
                ball.SetDir(-1, -1);
            }

            if (ballLPos >= first && ballLPos < second) {
                ball.SetDir(-1, -ball.GetDirY());
            }

            if (ballLPos >= second && ballLPos < third) {
                ball.SetDir(0, -1);
            }

            if (ballLPos >= third && ballLPos < fourth) {
                ball.SetDir(1, -ball.GetDirY());
            }

            if (ballLPos > fourth) {
                ball.SetDir(1, -1);
            }
        }

        for (var tile : m_ModelRef.GetTiles()) {
            if ((ball.getRect()).intersects(tile.getRect())) {

                final int ballLeft = (int) ball.getRect().getMinX();
                final int ballHeight = (int) ball.getRect().getHeight();
                final int ballWidth = (int) ball.getRect().getWidth();
                final int ballTop = (int) ball.getRect().getMinY();

                final var pointRight = new Point(ballLeft + ballWidth + 1, ballTop);
                final var pointLeft = new Point(ballLeft - 1, ballTop);
                final var pointTop = new Point(ballLeft, ballTop - 1);
                final var pointBottom = new Point(ballLeft, ballTop + ballHeight + 1);

                if (!tile.IsDestroyed()) {
                    if (tile.getRect().contains(pointRight)) {
                        ball.SetDir(-1, ball.GetDirY());
                    } else if (tile.getRect().contains(pointLeft)) {
                        ball.SetDir(1, ball.GetDirY());
                    }

                    if (tile.getRect().contains(pointTop)) {
                        ball.SetDir(ball.GetDirX(), 1);
                    } else if (tile.getRect().contains(pointBottom)) {
                        ball.SetDir(ball.GetDirX(), -1);
                    }

                    m_ModelRef.GetGameContext().IncrementScore();
                    tile.SetDestroyed(true);
                }
            }
        }

        if (ball.getY() >= paddle.getY()) {
            m_ModelRef.SetStopRequested(true);
        }
    }


}
