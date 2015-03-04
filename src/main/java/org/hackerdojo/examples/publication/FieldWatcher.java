package org.hackerdojo.examples.publication;

final class FieldWatcher implements Runnable {

    @Override
    public void run() {
        while (true) {
            if (Main.checker == null) {
                continue;
            }

            Main.checker.check();
        }
    }
}
