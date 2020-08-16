package the.wind.library;

import org.junit.Assert;
import org.junit.Test;

import java.util.Timer;
import java.util.TimerTask;

import the.wind.library.async.Promise;
import the.wind.library.utils.CWMathUtils;

public class PromiseTest {

    private final Object syncObject = new Object();
    private Timer mTimer = new Timer();

    @Test
    public void printSuccess() throws InterruptedException {
        System.out.println("-------->> Testcase: print success");
        Promise.wrap(new PrintTextPromise().print("color"));
        Promise.wrap(new PrintTextPromise().print("color")).then(new Promise.OnSuccessListener<String, String>() {
            @Override
            public Promise.IPromise<String> onSuccess(String data) {
                Assert.assertEquals("color", data);
                return null;
            }

        }).finish(new Promise.OnFinishListener() {
            @Override
            public void onFinish(Throwable throwable, Object data) {
                Assert.assertEquals("color", data);
                synchronized (syncObject) {
                    syncObject.notify();
                }
            }
        });

        synchronized (syncObject) {
            syncObject.wait();
        }
    }

    @Test
    public void printSuccessWithoutFinishListener() throws InterruptedException {
        System.out.println("-------->> Testcase: print success without finish listener");
        Promise.wrap(new PrintTextPromise().print("color")).then(new Promise.OnSuccessListener<String, String>() {
            @Override
            public Promise.IPromise<String> onSuccess(String data) {
                Assert.assertEquals("color", data);
                synchronized (syncObject) {
                    syncObject.notify();
                }
                return null;
            }
        });

        synchronized (syncObject) {
            syncObject.wait();
        }
    }

    @Test
    public void printSuccessWithFinishListenerOnly() throws InterruptedException {
        System.out.println("-------->> Testcase: print success with finish only");
        Promise.wrap(new PrintTextPromise().print("color")).finish(new Promise.OnFinishListener() {
            @Override
            public void onFinish(Throwable throwable, Object data) {
                Assert.assertEquals("color", data);
                synchronized (syncObject) {
                    syncObject.notify();
                }
            }
        });

        synchronized (syncObject) {
            syncObject.wait();
        }
    }

    @Test
    public void printMultipleSuccess() throws InterruptedException {
        System.out.println("-------->> Testcase: print multiple success");

        /*
         * Print text and number as the following order
         * color -> the -> exception -> 69 -> 96 -> wind
         */
        Promise.wrap(new PrintTextPromise().print("color")).then(new Promise.OnSuccessListener<String, String>() {
            @Override
            public Promise.IPromise<String> onSuccess(String data) {
                Assert.assertEquals("color", data);
                return new PrintTextPromise().print("the");
            }

        }).then(new Promise.OnSuccessListener<String, Integer>() {
            @Override
            public Promise.IPromise<Integer> onSuccess(String data) {
                Assert.assertEquals("the", data);
                return new PrintNumberPromise().print(69);
            }

        }).then(new Promise.OnSuccessListener<Integer, Integer>() {
            @Override
            public Promise.IPromise<Integer> onSuccess(Integer data) {
                Assert.assertEquals(69, data, 0);
                return new PrintNumberPromise().print(96);
            }

        }).then(new Promise.OnSuccessListener<Integer, String>() {
            @Override
            public Promise.IPromise<String> onSuccess(Integer data) {
                Assert.assertEquals(96, data, 0);
                return new PrintTextPromise().print("wind");
            }

        }).then(new Promise.OnSuccessListener<String, String>() {
            @Override
            public Promise.IPromise<String> onSuccess(String data) {
                Assert.assertEquals("wind", data);
                return null; // no promise returned -> finish

            }
        }).finish(new Promise.OnFinishListener() {
            @Override
            public void onFinish(Throwable throwable, Object data) {
                Assert.assertEquals("wind", data);
                System.out.println("Finally!");
                synchronized (syncObject) {
                    syncObject.notify();
                }
            }
        });

        synchronized (syncObject) {
            syncObject.wait();
        }
    }


    @Test
    public void printException() throws InterruptedException {
        System.out.println("-------->> Testcase: print text exception");
        Promise.wrap(new PrintTextPromise().print(null));
        Promise.wrap(new PrintTextPromise().print(null)).exception(new Promise.OnExceptionListener() {
            @Override
            public void onException(Throwable throwable) {
                Assert.assertTrue(throwable instanceof NullPointerException);
            }

        }).finish(new Promise.OnFinishListener() {
            @Override
            public void onFinish(Throwable throwable, Object data) {
                Assert.assertNull(data);
                Assert.assertTrue(throwable instanceof NullPointerException);
                synchronized (syncObject) {
                    syncObject.notify();
                }
            }
        });

        synchronized (syncObject) {
            syncObject.wait();
        }
    }

    @Test
    public void printExceptionWithoutFinishListener() throws InterruptedException {
        System.out.println("-------->> Testcase: print text exception");
        Promise.wrap(new PrintTextPromise().print(null)).exception(new Promise.OnExceptionListener() {
            @Override
            public void onException(Throwable throwable) {
                System.out.println("Exception!");
                Assert.assertTrue(throwable instanceof NullPointerException);
                synchronized (syncObject) {
                    syncObject.notify();
                }
            }
        });

        synchronized (syncObject) {
            syncObject.wait();
        }
    }

    @Test
    public void printExceptionWithFinishOnly() throws InterruptedException {
        System.out.println("-------->> Testcase: print text exception");
        Promise.wrap(new PrintTextPromise().print(null));
        Promise.wrap(new PrintTextPromise().print(null)).finish(new Promise.OnFinishListener() {
            @Override
            public void onFinish(Throwable throwable, Object data) {
                Assert.assertNull(data);
                Assert.assertTrue(throwable instanceof NullPointerException);
                synchronized (syncObject) {
                    syncObject.notify();
                }
            }
        });

        synchronized (syncObject) {
            syncObject.wait();
        }
    }

    @Test
    public void printMultipleValueException() throws InterruptedException {
        System.out.println("-------->> Testcase: print multiple exception promise");
        Promise.wrap(new PrintTextPromise().print(null)).exception(new Promise.OnExceptionListener() {
            @Override
            public void onException(Throwable throwable) {
                Assert.assertTrue(throwable instanceof NullPointerException);
            }

        }).exception(new Promise.OnExceptionListener() {
            @Override
            public void onException(Throwable throwable) {
                System.out.println("Exception!");
                Assert.assertTrue(throwable instanceof NullPointerException);
            }

        }).finish(new Promise.OnFinishListener() {
            @Override
            public void onFinish(Throwable throwable, Object data) {
                Assert.assertNull(data);
                Assert.assertTrue(throwable instanceof NullPointerException);
                synchronized (syncObject) {
                    syncObject.notify();
                }
            }
        });

        synchronized (syncObject) {
            syncObject.wait();
        }
    }

    @Test
    public void stopOtherPromiseWhenAnExceptionOccur() throws InterruptedException {
        System.out.println("-------->> Testcase: stop other promise when an exception occur");
        Promise.wrap(new PrintTextPromise().print("color")).then(new Promise.OnSuccessListener<String, String>() {
            @Override
            public Promise.IPromise<String> onSuccess(String data) {
                Assert.assertEquals("color", data);

                // this will trigger an exception
                // --->> other next promise will not be resolved
                return new PrintTextPromise().print(null);
            }

        }).then(new Promise.OnSuccessListener<String, Integer>() /* this promise will be skipped */ {
            @Override
            public Promise.IPromise<Integer> onSuccess(String data) {
                Assert.fail();
                return new PrintNumberPromise().print(69);
            }

        }).then(new Promise.OnSuccessListener<Integer, Integer>() /* this promise will be skipped */ {
            @Override
            public Promise.IPromise<Integer> onSuccess(Integer data) {
                return new PrintNumberPromise().print(96);
            }

        }).exception(new Promise.OnExceptionListener() {
            @Override
            public void onException(Throwable throwable) {
                System.out.println("Exception!");
                Assert.assertTrue(throwable instanceof NullPointerException);
            }

        }).finish(new Promise.OnFinishListener() {
            @Override
            public void onFinish(Throwable throwable, Object data) {
                Assert.assertTrue(throwable instanceof NullPointerException);
                Assert.assertNull(data);
                System.out.println("Finally!");
                synchronized (syncObject) {
                    syncObject.notify();
                }
            }
        });

        synchronized (syncObject) {
            syncObject.wait();
        }
    }

    /* ---------------------- OVERRIDE ----------------------- */

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /* ---------------------- METHOD ------------------------- */

    /* ---------------------- INNER CLASS -------------------- */

    private class PrintTextPromise implements Promise.IPromise<String> {

        private Promise<String> mPromise = new Promise<>();

        @Override
        public Promise<String> promise() {
            return mPromise;
        }

        public PrintTextPromise print(final String text) {
            final int delay = CWMathUtils.random(100, 200);
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (text == null || text.trim().isEmpty()) {
                        System.out.println("text - exception" + " - " + delay);
                        mPromise.resolve(new NullPointerException());
                    } else {
                        System.out.println("text - " + text + " - " + delay);
                        mPromise.resolve(text);
                    }
                }
            }, delay);
            return this;
        }
    }

    private class PrintNumberPromise implements Promise.IPromise<Integer> {

        private Promise<Integer> mPromise = new Promise<>();

        @Override
        public Promise<Integer> promise() {
            return mPromise;
        }

        public PrintNumberPromise print(final Integer number) {
            final int delay = CWMathUtils.random(100, 200);
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (number == null) {
                        System.out.println("number - exception" + " - " + delay);
                        mPromise.resolve(new NullPointerException());
                    } else {
                        System.out.println("number - " + number + " - " + delay);
                        mPromise.resolve(number);
                    }
                }
            }, delay);
            return this;
        }
    }
}
