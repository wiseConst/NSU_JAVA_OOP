package GUI;

import Factory.Factory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class FactoryObserver extends JFrame {
    public Integer m_UpdateRate = 1000 / 60;

    public FactoryObserver(Factory factoryRef) {
        setTitle("CAR FACTORY");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        setSize(400, 250);

        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
                factoryRef.Shutdown();
            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }

        });

        JPanel panel = new JPanel();
        panel.setDoubleBuffered(true);
        panel.setBackground(new Color(73, 173, 245));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel totalCarsProducedLabel = new JLabel();
        panel.add(totalCarsProducedLabel);

        JLabel carsInStorageLabel = new JLabel();
        panel.add(carsInStorageLabel);

        JLabel bodyKitsInStorageLabel = new JLabel();
        panel.add(bodyKitsInStorageLabel);

        JLabel enginesInStorageLabel = new JLabel();
        panel.add(enginesInStorageLabel);

        JLabel accessoriesInStorageLabel = new JLabel();
        panel.add(accessoriesInStorageLabel);

        {
            JLabel accessorySupplierDelayLabel = new JLabel("Current accessory supplier delay: " + factoryRef.getCurrentAccessorySupplierDelay());
            JSlider accessorySupplierDelay = new JSlider(JSlider.HORIZONTAL, 1000, 5000, factoryRef.getCurrentAccessorySupplierDelay());
            accessorySupplierDelay.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    accessorySupplierDelayLabel.setText("Current accessory supplier delay: " + factoryRef.getCurrentAccessorySupplierDelay());
                    factoryRef.setAccessorySupplierDelay(accessorySupplierDelay.getValue());
                }
            });
            panel.add(accessorySupplierDelay);
            panel.add(accessorySupplierDelayLabel);
        }

        {
            JLabel bodyKitSupplierDelayLabel = new JLabel("Current bodykit supplier delay: " + factoryRef.getCurrentBodyKitSupplierDelay());
            JSlider bodyKitSupplierDelay = new JSlider(JSlider.HORIZONTAL, 1000, 5000, factoryRef.getCurrentBodyKitSupplierDelay());
            bodyKitSupplierDelay.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    bodyKitSupplierDelayLabel.setText("Current bodykit supplier delay: " + factoryRef.getCurrentBodyKitSupplierDelay());
                    factoryRef.setBodyKitSupplierDelay(bodyKitSupplierDelay.getValue());
                }
            });
            panel.add(bodyKitSupplierDelay);
            panel.add(bodyKitSupplierDelayLabel);
        }

        {
            JLabel engineSupplierDelayLabel = new JLabel("Current engine supplier delay: " + factoryRef.getCurrentEngineSupplierDelay());
            JSlider engineSupplierDelay = new JSlider(JSlider.HORIZONTAL, 1000, 5000, factoryRef.getCurrentEngineSupplierDelay());
            engineSupplierDelay.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    engineSupplierDelayLabel.setText("Current engine supplier delay: " + factoryRef.getCurrentEngineSupplierDelay());
                    factoryRef.setEngineSupplierDelay(engineSupplierDelay.getValue());
                }
            });
            panel.add(engineSupplierDelay);
            panel.add(engineSupplierDelayLabel);
        }

        {
            JLabel dealerDelayLabel = new JLabel("Current dealer supplier delay: " + factoryRef.getCurrentDealerDelay());
            JSlider dealerDelay = new JSlider(JSlider.HORIZONTAL, 1000, 5000, factoryRef.getCurrentDealerDelay());
            dealerDelay.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    dealerDelayLabel.setText("Current dealer supplier delay: " + factoryRef.getCurrentDealerDelay());
                    factoryRef.setDealerDelay(dealerDelay.getValue());
                }
            });
            panel.add(dealerDelay);
            panel.add(dealerDelayLabel);
        }

        add(panel);

        setVisible(true);
        Timer timer = new Timer(m_UpdateRate, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                totalCarsProducedLabel.setText("Total cars produced: " + factoryRef.getTotalCarsProduced());
                carsInStorageLabel.setText("Cars in storage: " + factoryRef.getCurrentProducedCarsCount());
                bodyKitsInStorageLabel.setText("BodyKits in storage: " + factoryRef.getCurrentProducedBodyKitsCount());
                enginesInStorageLabel.setText("Engines in storage: " + factoryRef.getCurrentProducedEngineCount());
                accessoriesInStorageLabel.setText("Accessories in storage: " + factoryRef.getCurrentAccessoriesCount());
                panel.repaint();
            }
        });


        timer.start();
        factoryRef.Run();
    }

}
