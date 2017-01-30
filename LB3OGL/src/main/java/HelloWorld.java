import org.joml.Math;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.windows.PIXELFORMATDESCRIPTOR;
import org.lwjgl.system.windows.User32;
import org.lwjgl.system.windows.WNDCLASSEX;
import org.lwjgl.system.windows.WindowsLibrary;
import struct.Edge;
import struct.Face;
import struct.Model;
import struct.Point;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Vector;

import static java.lang.Math.pow;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.WGL.*;
import static org.lwjgl.system.Checks.check;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memPutAddress;
import static org.lwjgl.system.windows.GDI32.*;
import static org.lwjgl.system.windows.User32.*;
import static org.lwjgl.system.windows.WindowsUtil.windowsThrowException;

/**
 * Created by Алексей on 22.01.2017.
 */
public class HelloWorld {
    private static final float[] IDENTITY_MATRIX = new float[]
            {
                    1.0f, 0.0f, 0.0f, 0.0f,
                    0.0f, 1.0f, 0.0f, 0.0f,
                    0.0f, 0.0f, 1.0f, 0.0f,
                    0.0f, 0.0f, 0.0f, 1.0f
            };

    private static final FloatBuffer matrix = BufferUtils.createFloatBuffer(16);
    private static final float[] forward = new float[3];
    private static final float[] side = new float[3];
    private static final float[] up = new float[3];
    private static final int RANDOM_MAX = 627679;
    static final int W_WIDTH = 1024;
    static final int W_HEIGHT = 512;
    float _NEARZ = 0.1f;
    float _FARZ = 100.0f;
    int winWidth = W_WIDTH;
    int winHeight = W_HEIGHT;
    Model model;
    Vector3f eye = new Vector3f(0, 0, 6);

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

    /**
     * Sets the window title
     *
     * @param title New window title
     */
    public void setTitle(CharSequence title) {
        glfwSetWindowTitle(window, title);
    }

    public void init() {
        try {
            model = new Model("/teapot.obj");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        eye = new Vector3f(0, 0, 6);
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        //glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        //  glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_DEPTH_BITS, 32);
        setDcPixelFormat();
        //glfwWindowHint();

        // Create the window
        window = glfwCreateWindow(winWidth, winHeight, "Hello World!", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            }
            if (key == GLFW_KEY_W && (action == GLFW_PRESS || action == GLFW_REPEAT)) {
                eye.z = eye.z - 0.1f;
            } else if (key == GLFW_KEY_S && (action == GLFW_PRESS || action == GLFW_REPEAT)) {
                eye.z = eye.z + 0.1f;
            }
            if (key == GLFW_KEY_A && (action == GLFW_PRESS || action == GLFW_REPEAT)) {
                eye.x = eye.x - 0.1f;
            } else if (key == GLFW_KEY_D && (action == GLFW_PRESS || action == GLFW_REPEAT)) {
                eye.x = eye.x + 0.1f;
            }
            if (key == GLFW_KEY_Z && (action == GLFW_PRESS || action == GLFW_REPEAT)) {
                eye.y = eye.y - 0.1f;
            } else if (key == GLFW_KEY_X && (action == GLFW_PRESS || action == GLFW_REPEAT)) {
                eye.y = eye.y + 0.1f;
            }
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
        GL.createCapabilities();
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    void setDcPixelFormat() {
        long hdc = wglGetCurrentDC();
        if (hdc != NULL) {

        }
        short classAtom = 0;
        long hwnd = NULL;
        long hglrc = NULL;
        try (MemoryStack stack = stackPush()) {
            WNDCLASSEX wc = WNDCLASSEX.callocStack(stack)
                    .cbSize(WNDCLASSEX.SIZEOF)
                    .style(CS_HREDRAW | CS_VREDRAW)
                    .hInstance(WindowsLibrary.HINSTANCE)
                    .lpszClassName(stack.UTF16("WGL"));

            memPutAddress(
                    wc.address() + WNDCLASSEX.LPFNWNDPROC,
                    User32.Functions.DefWindowProc
            );

            classAtom = RegisterClassEx(wc);
            if (classAtom == 0)
                throw new IllegalStateException("Failed to register WGL window class");

            hwnd = check(nCreateWindowEx(
                    0, classAtom & 0xFFFF, NULL,
                    WS_OVERLAPPEDWINDOW | WS_CLIPCHILDREN | WS_CLIPSIBLINGS,
                    0, 0, 1, 1,
                    NULL, NULL, NULL, NULL
            ));

            hdc = check(GetDC(hwnd));

            PIXELFORMATDESCRIPTOR pfd = PIXELFORMATDESCRIPTOR.callocStack(stack)
                    .nSize((short) PIXELFORMATDESCRIPTOR.SIZEOF)
                    .nVersion((short) 1)
                    .dwFlags(PFD_DRAW_TO_WINDOW |
                            PFD_SUPPORT_OPENGL | PFD_DOUBLEBUFFER)
                    .iPixelType(PFD_TYPE_RGBA)
                    .cColorBits((byte) 24)
                    .cDepthBits((byte) 32)
                    .iLayerType(PFD_MAIN_PLANE);

            int pixelFormat = ChoosePixelFormat(hdc, pfd);
            if (pixelFormat == 0)
                windowsThrowException("Failed to choose an OpenGL-compatible pixel format");

            if (!SetPixelFormat(hdc, pixelFormat, pfd))
                windowsThrowException("Failed to set the pix");

        }
        {
            if (hglrc != NULL) {
                wglMakeCurrent(NULL, NULL);
                wglDeleteContext(hglrc);
            }

            if (hwnd != NULL)
                DestroyWindow(hwnd);

            if (classAtom != 0)
                nUnregisterClass(classAtom & 0xFFFF, WindowsLibrary.HINSTANCE);
        }
    }

    public void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);
        glShadeModel(GL_FLAT);
        glClearDepth(1.0);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glEnable(GL_COLOR_MATERIAL);

        glEnable(GL_LIGHT0);
        float lightpos[] = {0.0f, 0.5f, 0.0f, 0.0f};
        glLightfv(GL_LIGHT0, GL_POSITION, lightpos);

        glClearColor(178.0f / 255.0f, 216.0f / 255.0f, 237.0f / 255.0f, 1.0f);
        glEnable(GL_SCISSOR_TEST);
        // Пока не нажата клавиша ESC делай.
        while (!glfwWindowShouldClose(window)) {
            // создаем окно
            glScissor(0, 0, winWidth / 2, winHeight);
            //glClearColor(178.0f / 255.0f, 216.0f / 255.0f, 237.0f / 255.0f, 1.0f);
            // очистка буфера кадров
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // создаем второе окно
            glScissor(winWidth / 2, 0, winWidth / 2, winHeight);
            // указывает значение цветового буфера
            glClearColor(1, 1, 1, 1);
            // очистка буфера кадров
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // флаг гоорящий о том освещать модель или нет
            model.setLit(true);
            // флаг гоорящий о том рисовать у  модели контур или нет
            model.setDrawContour(false);
            // цвет модели
            model.setColor(new Vector3f(1.0f, 0.0f, 1.0f));
            // glClearColor(1, 1, 1, 1);
            renderModel(0, 0, winWidth / 2 - 1, winHeight - 1, model, eye);

            model.setLit(false);
            model.setDrawContour(true);
            model.setColor(new Vector3f(1, 1, 1));

            // glClearColor(1, 1, 1, 1);
            renderModel(winWidth / 2, 0, winWidth / 2 - 1, winHeight - 1, model, eye);

            // Сменяем задний и передний буферы
            glfwSwapBuffers(window); // swap the color buffers
            // Опросить события окна и вызвать калбэк для нх
            glfwPollEvents();
        }
    }


    public static void main(String[] args) {
        new HelloWorld().run();
    }

    static final int RAND_BORDER = (int) (RANDOM_MAX * 0.03);
    // мы будем игнорировать из расмотрения случайно часть ребёр, которые на прошлой итерации не были визуализированы
    // из-за чего картинка чуть-чуть бликует при передвижении камеры
    // зато значительно растёт FPS
    Vector<Face> faces;
    Vector<Edge> edges;
    Edge edge;
    Vector3f na;
    Vector3f nb;
    Vector3f v0;
    Face faceA;
    Face faceB;
    float scale = 0.999F;
    void drawContour(Model model, Vector3f eye) {
        // задать ширини линии
        glLineWidth(5.0F);

        // задаем цвет линии
        glColor3f(0.0F, 0.0F, 0.0F);

        edges = model.getEdges();
        int size = model.getEdges().size();
        for (int i = 0; i < size; i++) {
            edge = edges.get(i);
            // получаем список граней для итого ребра
            faces = edge.getFaces();
            if (faces.size() >= 2 ){//&& (edge.isWasVisible() || (java.lang.Math.random() * RAND_BORDER < RAND_BORDER))) {
                faceA = faces.get(0);
                faceB = faces.get(1);

                na = faceA.getNormal();
                nb = faceB.getNormal();

                //v0 = edge.getPointA().getV();
                v0  = edge.getPointB().getV();
                v0 = subVec(v0, eye);

                double angle = getAngle(na, nb);
               // boolean a = (na.dot(v0)) > 0;
               // boolean b = (nb.dot(v0)) > 0;
                float a = na.dot(v0);
                float b = nb.dot(v0);

                if (a * b <= 0.15 || angle > 1) {
                    edge.setWasVisible(true);
                    glBegin(GL_LINES);
                    p = edge.getPointA();
                    Vector3f v = p.getV();

                    glVertex3f(v.x * scale, v.y * scale, v.z * scale);
                    p = edge.getPointB();
                    v = p.getV();
                    glVertex3f(v.x * scale, v.y * scale, v.z * scale);

                    glEnd();
                } else {
                   //edge.setWasVisible(false);
                }
            }
        }
    }

    private Vector3f subVec(Vector3f eye, Vector3f v0) {
        return new Vector3f(eye.x - v0.x, eye.y - v0.y, eye.z - v0.z);
    }

    private double getAngle(Vector3f v1, Vector3f v2) {
        double scalar = v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
        return Math.acos(scalar / (getDistance(new Vector3f(), v1) * getDistance(new Vector3f(), v2)));
    }

    double myDot(Vector3f v1, Vector3f v) {
        return ( v1.x * v.x + v1.y * v.y + v1.z * v.z);
    }

    private double getDistance(Vector3f p1, Vector3f p2) {
        return Math.sqrt(pow(p1.x - p2.x, 2.0) + pow(p1.y - p2.y, 2.0) + pow(p1.z - p2.z, 2.0));
    }

    Point p;

    void renderModel(int vpX, int vpY, int vpWidth, int vpHeight, Model model, Vector3f eye) {
        // включить освещение модели
        if (model.isLit()) {
            glEnable(GL_LIGHTING);
            // glEnable(GL_LIGHT0);
        }
        // задаем параметры окна в котором работаем
        glScissor(vpX, vpY, vpWidth, vpHeight);
        // задает аффинное преобразование х и у из нормализованного устройства в координаты окна.
        glViewport(vpX, vpY, vpWidth, vpHeight);
        // задаем матрицу для проссмотра проекции
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        // создаем матрицу перспективной проекции (камера)
        perspectiveGL(60.0f, vpWidth / vpHeight, _NEARZ, _FARZ);

        // задаем матричную операцию для просмотра модели
        glMatrixMode(GL_MODELVIEW);
        // заменяем текущую матрицу на единичную
        glLoadIdentity();

//        определить преобразование для прасмотра
        gluLookAt(eye.x, eye.y, eye.z, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
       /* glTranslatef(eye.x, eye.y, eye.z);
        glRotatef(angelY, 0, 1, 0);
        glRotatef(angelX, 1, 0, 0);*/

        glColor4f(model.getColor().x, model.getColor().y, model.getColor().z, 1);

//        протолкнуть стек текущи матриц
        glPushMatrix();
        for (int i = 0; i < model.getFaces().size(); i++) {
            // начинаем
            glBegin(GL_TRIANGLES);
            for (int j = 0; j < 3; j++) {
                // получаем точку текущей грани
                p = model.getFaces().get(i).getPoints()[j];
                // задаем нормаль
                glNormal3f(p.getVn().x, p.getVn().y, p.getV().z);
                // указываем вершину
                glVertex3f(p.getV().x, p.getV().y, p.getV().z);
            }
            // закончили рисовать
            glEnd();
        }
        // загружаем матрицу из стека
        glPopMatrix();

        // надо ли рисовать контур
        if (model.isDrawContour()) {
            drawContour(model, eye);
        }

        // подсвечиваем фигуру
        if (model.isLit()) {
            glDisable(GL_LIGHTING);
        }
    }

    private static void perspectiveGL(float fovy, float aspect, float zNear, float zFar) {
        float sine, cotangent, deltaZ;
        float radians = fovy / 2 * (float) Math.PI / 180;

        deltaZ = zFar - zNear;
        sine = (float) Math.sin(radians);

        if ((deltaZ == 0) || (sine == 0) || (aspect == 0)) {
            return;
        }

        cotangent = (float) Math.cos(radians) / sine;

        __gluMakeIdentityf(matrix);

        matrix.put(0 * 4 + 0, cotangent / aspect);
        matrix.put(1 * 4 + 1, cotangent);
        matrix.put(2 * 4 + 2, -(zFar + zNear) / deltaZ);
        matrix.put(2 * 4 + 3, -1);
        matrix.put(3 * 4 + 2, -2 * zNear * zFar / deltaZ);
        matrix.put(3 * 4 + 3, 0);

        GL11.glMultMatrixf(matrix);
    }

    public static void gluLookAt(
            float eyex,
            float eyey,
            float eyez,
            float centerx,
            float centery,
            float centerz,
            float upx,
            float upy,
            float upz) {
        float[] forward = HelloWorld.forward;
        float[] side = HelloWorld.side;
        float[] up = HelloWorld.up;

        forward[0] = centerx - eyex;
        forward[1] = centery - eyey;
        forward[2] = centerz - eyez;

        up[0] = upx;
        up[1] = upy;
        up[2] = upz;

        normalize(forward);

        		/* Side = forward x up */
        cross(forward, up, side);
        normalize(side);

        		/* Recompute up as: up = side x forward */
        cross(side, forward, up);

        __gluMakeIdentityf(matrix);
        matrix.put(0 * 4 + 0, side[0]);
        matrix.put(1 * 4 + 0, side[1]);
        matrix.put(2 * 4 + 0, side[2]);

        matrix.put(0 * 4 + 1, up[0]);
        matrix.put(1 * 4 + 1, up[1]);
        matrix.put(2 * 4 + 1, up[2]);

        matrix.put(0 * 4 + 2, -forward[0]);
        matrix.put(1 * 4 + 2, -forward[1]);
        matrix.put(2 * 4 + 2, -forward[2]);

        GL11.glMultMatrixf(matrix);
        glTranslatef(-eyex, -eyey, -eyez);
    }

    protected static void cross(float[] v1, float[] v2, float[] result) {
        result[0] = v1[1] * v2[2] - v1[2] * v2[1];
        result[1] = v1[2] * v2[0] - v1[0] * v2[2];
        result[2] = v1[0] * v2[1] - v1[1] * v2[0];
    }

    private static void __gluMakeIdentityf(FloatBuffer m) {
        int oldPos = m.position();
        m.put(IDENTITY_MATRIX);
        m.position(oldPos);
    }

    protected static float[] normalize(float[] v) {
        float r;

        r = (float) Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
        if (r == 0.0)
            return v;

        r = 1.0f / r;

        v[0] *= r;
        v[1] *= r;
        v[2] *= r;

        return v;
    }
}
