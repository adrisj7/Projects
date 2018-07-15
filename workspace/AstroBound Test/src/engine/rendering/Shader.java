package engine.rendering;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import engine.util.FileHandler;

public class Shader {
    private int program;
    private int vertexShader;
    private int fragmentShader;
    
    public Shader(String fname) {
        program = GL20.glCreateProgram();

        // Create slots for our shaders
        vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);

        // Read and compile our shaders
        GL20.glShaderSource(vertexShader, FileHandler.readTextFile(fname + ".vs"));
        GL20.glCompileShader(vertexShader);
        if(GL20.glGetShaderi(vertexShader, GL20.GL_COMPILE_STATUS) != 1) {
            System.err.println("[Shader.java] VERTEX SHADER COMPILE ERROR! Info log: " + GL20.glGetShaderInfoLog(vertexShader));
            System.exit(1);
        }
        GL20.glShaderSource(fragmentShader, FileHandler.readTextFile(fname + ".fs"));
        GL20.glCompileShader(fragmentShader);
        if(GL20.glGetShaderi(fragmentShader, GL20.GL_COMPILE_STATUS) != 1) {
            System.err.println("[Shader.java] FRAGMENT SHADER COMPILE ERROR! Info log: " + GL20.glGetShaderInfoLog(fragmentShader));
            System.exit(1);
        }

        // Attach shaders
        GL20.glAttachShader(program, vertexShader);
        GL20.glAttachShader(program, fragmentShader);

        // Used in the code I think
        GL20.glBindAttribLocation(program, 0, "vertices");
        GL20.glBindAttribLocation(program, 1, "textures");

        // Link and validate, and catch errors
        GL20.glLinkProgram(program);
        if(GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) != 1) {
            System.err.println("[Shader.java] PROGRAM LINKING ERROR! Info log: " + GL20.glGetProgramInfoLog(fragmentShader));            
            System.exit(1);
        }
        GL20.glValidateProgram(program);
        if(GL20.glGetProgrami(program, GL20.GL_VALIDATE_STATUS) != 1) {
            System.err.println("[Shader.java] PROGRAM VALIDATION ERROR! Info log: " + GL20.glGetProgramInfoLog(fragmentShader));            
            System.exit(1);
        }        
    }

    /**
     * Clean up the shader
     */
    @Override
    protected void finalize() throws Throwable {
        GL20.glDetachShader(program, vertexShader);
        GL20.glDetachShader(program, fragmentShader);
        GL20.glDeleteShader(vertexShader);
        GL20.glDeleteShader(fragmentShader);
        GL20.glDeleteProgram(program);
        super.finalize();
    }
    
    /**
     * Binds this shader program
     */
    public void bind() {
        GL20.glUseProgram(program);
    }

    // Methods for setting uniforms
    public void setUniformInt(String name, int value) {
        int location = GL20.glGetUniformLocation(program, name);
        if (location != -1) {
            GL20.glUniform1i(location, value);
        }
    }
    public void setUniformFloat(String name, int value) {
        int location = GL20.glGetUniformLocation(program, name);
        if (location != -1) {
            GL20.glUniform1f(location, value);
        }
    }
    public void setUniformMatrix4f(String name, Matrix4f value) {
        int location = GL20.glGetUniformLocation(program, name);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(4 * 4);
        value.get(buffer);
        if (location != -1) {
            GL20.glUniformMatrix4fv(location,false,buffer);
        }
    }

}
