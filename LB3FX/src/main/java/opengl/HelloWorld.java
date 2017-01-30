package opengl;

import com.sun.javafx.geom.Vec3f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.windows.PIXELFORMATDESCRIPTOR;
import org.lwjgl.util.glu.GLU;
import struct.Edge;
import struct.Face;
import struct.Model;
import struct.Point;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Vector;

import static java.lang.Math.pow;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.windows.GDI32.*;

/**
 * Created by Алексей on 22.01.2017.
 */
public class HelloWorld {
    static final long W_WIDTH = 1024;
    static final long W_HEIGHT = 512;
    float _NEARZ = 0.1f;
    float _FARZ = 100.0f;
    long winWidth = W_WIDTH;
    long winHeight = W_HEIGHT;

    // The window handle
    private long window;

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {

        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);

      /*  glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);
        glShadeModel(GL_FLAT);
        glClearDepth(1.0);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glEnable(GL_COLOR_MATERIAL);

        glEnable(GL_LIGHT0);
        float[] lightpos = {0.0f, 0.5f, 0.0f, 0.0f};
        glLightfv(GL_LIGHT0, GL_POSITION, lightpos);

        glClearColor(178.0F / 255.0F, 216.0F / 255.0F, 237.0F / 255.0F, 1.0F);
        glEnable(GL_SCISSOR_TEST);*/
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
       /* GL.createCapabilities();*/

        // Set the clear color
      /*  glClearColor(1.0f, 0.0f, 0.0f, 0.0f);*/

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
       /* while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }*/
    }

    public static void main(String[] args) {
        new HelloWorld().run();
    }

    void setDcPixelFormat(long hDC){
        PIXELFORMATDESCRIPTOR pfd = new PIXELFORMATDESCRIPTOR(ByteBuffer.allocate( 1024 * 15 ));
        int iFormat;

        //TODO: спросить что это за херня
        //ZeroMemory(pfd, sizeof(pfd));
        //  pfd.nSize(sizeof(pfd));
        short version = 1;
        pfd.nVersion(version);
        pfd.dwFlags( PFD_DRAW_TO_WINDOW |
                PFD_SUPPORT_OPENGL | PFD_DOUBLEBUFFER);
        pfd.iPixelType( PFD_TYPE_RGBA);
        pfd.cColorBits( (byte) 24);
        pfd.cDepthBits((byte) 32);
        pfd.iLayerType(PFD_MAIN_PLANE);

        iFormat = ChoosePixelFormat(hDC, pfd);

        SetPixelFormat(hDC, iFormat, pfd);
    }

    void reSizeGlScene(int width, int height){
        if (height==0){
            height=1;
        }

        winWidth = width;
        winHeight = height;
    }

    void drawContour(Model model, Vec3f eye) {
        glLineWidth(3.0F);

        eye.normalize();

        glColor3f(0.0F, 0.0F, 0.0F);
        // ìû áóäåì èãíîðèðîâàòü èç ðàñìîòðåíèÿ ñëó÷àéíî ÷àñòü ðåá¸ð, êîòîðûå íà ïðîøëîé èòåðàöèè íå áûëè âèçóàëèçèðîâàíû
        // èç-çà ÷åãî êàðòèíêà ÷óòü-÷óòü áëèêóåò ïðè ïåðåäâèæåíèè êàìåðû
        // çàòî çíà÷èòåëüíî ðàñò¸ò FPS
        int RAND_BORDER = (int) (32767 * 0.03);
        Vector<Edge> edges;
        Vector<Face> faces;
        Edge edge;
        for (int i = 0; i < model.getEdges().size(); i++) {
            edges = model.getEdges();
            faces = edges.get(i).getFaces();
            edge = model.getEdges().get(i);
            if (faces.size() >= 2 && (edge.isWasVisible() || (Math.random() < RAND_BORDER))) {
                Face faceA = faces.get(0);
                Face faceB = faces.get(1);

                Vec3f na = faceA.getNormal();

                Vec3f nb = faceB.getNormal();

                Vec3f v0 = edge.getPointA().getV();
                v0 = subVec(eye, v0);

                boolean a = (na.dot(v0)) > 0;
                boolean b = (nb.dot(v0)) > 0;
                double angle = getAngle(na, nb);

                if (a ^ b || angle > 1) {
                    edge.setWasVisible(true);
                    Point p;
                    glBegin(GL_LINES);
                    double scale = 1;
                    p = edge.getPointA();
                    Vec3f v = p.getV();
                    glVertex3f(v.x, v.y, v.z);

                    p = edge.getPointB();
                    v = p.getV();
                    glVertex3f(v.x, v.y, v.z);
                    glEnd();
                } else {
                    edge.setWasVisible(false);
                }
            }
        }
    }

    private Vec3f subVec(Vec3f eye, Vec3f v0) {
        return new Vec3f(eye.x - v0.x, eye.y - v0.y, eye.z - v0.z);
    }

    private double getAngle(Vec3f v1, Vec3f v2) {
        double scalar = v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
        return Math.acos(scalar / (getDistance(new Vec3f(), v1) * getDistance(new Vec3f(), v2)));
    }

    private double getDistance(Vec3f p1, Vec3f p2) {
        return Math.sqrt(pow(p1.x - p2.x, 2.0) + pow(p1.y - p2.y, 2.0) + pow(p1.z - p2.z, 2.0));
    }

    void renderModel(int vpX, int vpY, int vpWidth, int vpHeight, Model model, Vec3f eye) {
        if (model.isLit()) {
            glEnable(GL_LIGHTING);
        }

        glScissor(vpX, vpY, vpWidth, vpHeight);
        glViewport(vpX, vpY, vpWidth, vpHeight);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        GLU.gluPerspective(60.0f, vpWidth / vpHeight, _NEARZ, _FARZ);

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        GLU.gluLookAt(eye.x, eye.y, eye.z, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);

        glColor4f(model.getColor().x, model.getColor().y, model.getColor().z, 1);

        glPushMatrix();
        for (int i = 0; i < model.getFaces().size(); i++) {
            glBegin(GL_TRIANGLES);
            for (int j = 0; j < 3; j++) {
                Point p;
                p = model.getFaces().get(i).getPoints()[j];
                glNormal3f(p.getVn().x, p.getVn().y, p.getV().z);
                glVertex3f(p.getV().x, p.getV().y, p.getV().z);
            }
            glEnd();
        }
        glPopMatrix();

        if (model.isDrawContour()) {
            drawContour(model, eye);
        }

        if (model.isLit()) {
            glDisable(GL_LIGHTING);
        }
    }

    float CalculateFPS() {
        //Íèæå ìû ñîçäàäèì íåñêîëüêî ñòàòè÷íûõ ïåðåìåííûõ, ò.ê. õîòèì, ÷òîáû îíè ñîõðàíÿëè ñâî¸
        //çíà÷åíèå ïîñëå çàâåðøåíèÿ ðàáîòû ô-èè. Ìû ìîãëè áû ñäåëàòü èõ ãëîáàëüíûìè, íî ýòî áóäåò
        //èçëèøíèì.
        //âîçâðàçùàåò -1, åñëè íå íàäî ïåðåðèñîâûâàòü, èíà÷å çíà÷åíèå fps

        float framesPerSecond = 0.0f;    //íàøè ôïñ
        float lastTime = System.nanoTime() * 0.001f;          //Òóò õðàíèòñÿ âðåìÿ, ïðîøåäøåå ñ ïîñëåäíåãî êàäðà

        //Òóò ìû ïîëó÷àåì òåêóùèé tick count è óìíîæàåì åãî íà 0.001 äëÿ êîíâåðòàöèè èç ìèëëèñåêóíä â ñåêóíäû.
        float currentTime = System.nanoTime() * 0.001f;
        //float currentTime = clock() * 0.001f;
        float FPS;

        //Óâåëè÷èâàåì ñ÷åò÷èê êàäðîâ
        ++framesPerSecond;

        //Òåïåðü âû÷òåì èç òåêóùåãî âðåìåíè ïîñëåäíåå çàïîìíåííîå âðåìÿ. Åñëè ðåçóëüòàò áîëüøå åäèíèöû,
        //ýòî çíà÷èò, ÷òî ñåêóíäà ïðîøëà è íóæíî âûâåñòè íîâûé FPS.
        if (currentTime - lastTime > 1.0f) {
            //Óñòàíàâëèâàåì lastTime â òåêóùåå âðåìÿ. Òåïåðü îíî áóäåò èñïîëüçîâàòñÿ êàê ïðåäèäóùåå âðåìÿ
            //äëÿ ñëåä. ñåêóíäû.
            lastTime = currentTime;

            // Óñòàíîâèì FPS äëÿ âûâîäà:
            FPS = framesPerSecond;

            //Ñáðîñèì FPS
            framesPerSecond = 0;

            return FPS;
        }
        return -1;
    }
}
