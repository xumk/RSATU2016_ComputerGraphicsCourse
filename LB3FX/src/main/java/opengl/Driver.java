package opengl;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Driver {

    public int width = 800, height = 600;

    public long window;
    public boolean running = false;

    public void init(){
        this.running = true;

        if(!glfwInit()){
            System.err.println("GLFW Window Handler Library failed to initialize");
        }

        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);

        window = glfwCreateWindow(width, height, "2D Pong", NULL, NULL);

        if(window == NULL){
            System.err.println("Could not create our window");
        }

        // creates a bytebuffer object 'vidmode' which then queries
        // to see what the primary monitor is.
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Sets the initial position of our game window.
        glfwSetWindowPos(window, 100, 100);
        // Sets the context of GLFW, this is vital for our program to work.
        glfwMakeContextCurrent(window);
        // finally shows our created window in all it's glory.
        glfwShowWindow(window);

        GL.createCapabilities();

        glClearColor(1.0f, 0.0f, 1.0f, 1.0f);
        glEnable(GL_DEPTH_TEST);
        System.out.println("OpenGL: " + glGetString(GL_VERSION));

    }

    public void render(){
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glfwSwapBuffers(window);
    }

    public void update(){
        glfwPollEvents();
    }

    public void run(){
        init();
        while(running){
            update();
            render();

            if(!glfwWindowShouldClose(window)){
                running = false;
            }
        }
    }


    public static void main(String args[]){
        System.out.println("2D Pong Tutorial");
        Driver driver = new Driver();
        driver.run();
    }

}
