package engine.rendering;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

public class Model {

    private int drawCount;

    private int vertexID;
    private int textureID;
    private int indiceID;

    public Model(float[] vertices, float[] textureUV, int[] indices) {
        drawCount = indices.length;

        FloatBuffer   vbuffer = createFBuffer(vertices);
        FloatBuffer   tbuffer = createFBuffer(textureUV);
        IntBuffer ibuffer = createIBuffer(indices);

        vertexID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexID);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vbuffer, GL15.GL_STATIC_DRAW); // Could be dynamic, if configured.

        textureID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, textureID);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, tbuffer, GL15.GL_STATIC_DRAW); // Could be dynamic, if configured.

        indiceID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indiceID);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, ibuffer, GL15.GL_STATIC_DRAW); // Could be dynamic, if configured.

        // Unbind our id's so nothing can change them
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); 
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
    }
    
    
    /**
     * Clean up the model
     */
    @Override
    protected void finalize() throws Throwable {
        GL15.glDeleteBuffers(vertexID);
        GL15.glDeleteBuffers(textureID);
        GL15.glDeleteBuffers(indiceID);
        super.finalize();
    }

    /**
     * Utility function that creates an OPENGL compatible float buffer from a data array
     * @param data
     * @return OPENGL compatible vertex/data float buffer
     */
    private FloatBuffer createFBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }
    private IntBuffer createIBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;        
    }

    /**
     * Draw our VBO
     */
    public void render() {
        // Enable
        GL20.glEnableVertexAttribArray(0); // Vertices at 0
        GL20.glEnableVertexAttribArray(1); // Textures at 0

        // Bind Vertex buffer
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexID);
        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);

        // Bind Texture buffer
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, textureID);
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);

        // Bind Indices buffer
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indiceID);

        // Draw!
        GL11.glDrawElements(GL11.GL_TRIANGLES, drawCount, GL11.GL_UNSIGNED_INT, 0);

        // Unbind and Disable
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL20.glDisableVertexAttribArray(0); // Vertices at 0
        GL20.glDisableVertexAttribArray(1); // Textures at 0
    }
}
