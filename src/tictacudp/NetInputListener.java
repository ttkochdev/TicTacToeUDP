package tictacudp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

class NetInputListener implements ActionListener {

        BlockingQueue<String> protQueue;

    public NetInputListener(BlockingQueue protQueue) {
        this.protQueue = protQueue;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        String queueTake = null;
        if (protQueue.isEmpty() == false) {
            try {
              queueTake = protQueue.take();
              //System.out.println("queueTake: "+queueTake);
            } catch (InterruptedException ex) {
                Logger.getLogger(NetInputListener.class.getName()).log(Level.SEVERE, null, ex);
            }
           
                //process the queueTake
                GameGlobals.processInput(queueTake);
        }
    }
}