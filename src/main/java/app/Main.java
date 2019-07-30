package app;

public class Main {

    public static void main(String[] args) {
        GenView genView = new GenView();
        GenModel genModel = new GenModel();
        GenController genController = new GenController(genView, genModel);
        genView.setVisible(true);

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
//                genController.closeMongo();
            }
        }, "Shutdown-thread"));
    }
}
