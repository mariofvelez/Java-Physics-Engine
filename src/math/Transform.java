package math;

import java.awt.geom.AffineTransform;
import java.util.Random;

/**
 * Provides fast computations for multidimensional linear transformations
 * @author Mario Velez
 *
 */
public class Transform {
	
	/**
	 * n*n matrix that holds transformation data
	 */
	public float[] data;
	/**
	 * the dimensions of the transform
	 */
	protected int dim;
	protected int len;
	
	private final float[] temp;
	
	/**
	 * 
	 * @param dim - number of dimensions
	 * @param identity - whether to initialize with all zeros or in identity form
	 */
	public Transform(int dim, boolean identity)
	{
		data = new float[dim*dim];
		len = dim*dim;
		this.dim = dim;
		if(identity)
			for(int i = 0; i < dim; i++)
				data[i*dim + i] = 1;
		temp = new float[dim];
	}
	public Transform(Transform copy)
	{
		this(copy.dim, true);
		for(int i = 0; i < data.length; ++i)
			data[i] = copy.data[i];
	}
	/**
	 * resets this transform to the identity matrix
	 */
	public void setIdentity()
	{
		for(int i = 0; i < len; i++)
			data[i] = 0;
		for(int i = 0; i < dim; i++)
			data[i*dim + i] = 1;
	}
	/**
	 * resets this transform to all zeroes
	 */
	public void setZero()
	{
		for(int i = 0; i < len; i++)
			data[i] = 0;
	}
	/**
	 * Sets the last column for position values
	 * @param x
	 */
	public void setPosition(float... x)
	{
		for(int i = 0; i < x.length; ++i)
			data[i*dim + (dim-1)] = x[i];
	}
	/**
	 * normalizes all the axes to unit vectors of length 1
	 */
	public void normalize()
	{
		for(int c = 0; c < dim; c++)
		{
			float len = 0;
			for(int r = 0; r < dim; r++)
				len += data[r*dim + c]*data[r*dim + c];
			len = (float) Math.sqrt(len);
			for(int r = 0; r < dim; r++)
				data[r*dim + c] /= len;
		}
	}
	/**
	 * projects the coordinates of the given vector to coordinates in real space. Essentially multiplies the matrix and 
	 * vector together.
	 * @param vec - the vector to project
	 */
	public void project(float[] vec)
	{
		for(int i = 0; i < dim; i++)
		{
			float n = 0;
			for(int j = 0; j < dim; j++)
				n += vec[j]*data[i*dim + j];
			temp[i] = n;
		}
		for(int i = 0; i < dim; i++)
			vec[i] = temp[i];
	}
	public void project2D(Vec2d vec)
	{
		for(int i = 0; i < 2; i++)
			temp[i] = vec.x*data[i*dim] + vec.y*data[i*dim + 1] + data[i*dim + 2];
		vec.x = temp[0];
		vec.y = temp[1];
	}
	public void project3D(Vec3d vec)
	{
		for(int i = 0; i < dim; i++)
			temp[i] = vec.x*data[i*dim] + vec.y*data[i*dim + 1] + vec.z*data[i*dim + 2];
		vec.x = temp[0];
		vec.y = temp[1];
		vec.z = temp[2];
	}
	/**
	 * Projects a vector without the translation transformation
	 * @param vec - the vector to transform
	 */
	public void projectVector(Vec2d vec)
	{
		for(int i = 0; i < dim; i++)
			temp[i] = vec.x*data[i*dim] + vec.y*data[i*dim + 1];
		vec.x = temp[0];
		vec.y = temp[1];
	}
	public void set(int r, int c, float value)
	{
		data[r*dim + c] = value;
	}
	/**
	 * Fills every value in this transform with the value n
	 * @param n - value to fill with
	 */
	public void fill(float n)
	{
		for(int i = 0; i < len; ++i)
			data[i] = n;
	}
	/**
	 * sets the values in this matrix to match the values in the parameter.
	 * @param data - the values to set this matrix to
	 */
	public void setData(float[] data)
	{
		for(int i = 0; i < len; i++)
			this.data[i] = data[i];
	}
	public float get(int r, int c)
	{
		return data[r*dim + c];
	}
	public float[] getData()
	{
		return data;
	}
	/**
	 * multiplies each value in this matrix by this number, resulting in a scaled transformation
	 * @param n - the number to multiply by
	 */
	public void mult(float n)
	{
		for(int i = 0; i < len; i++)
			data[i] *= n;
	}
	/**
	 * Alters this transform to the product of this and t
	 * @param t - the transform to apply
	 */
	public void mult(Transform t)
	{
		float[] mult = new float[len];
		for(int r = 0; r < dim; r++)
		{
			for(int c = 0; c < dim; c++)
			{
				float value = 0;
				for(int i = 0; i < dim; i++)
					value += data[r*dim + i]*t.data[i*dim + c];
				mult[r*dim + c] = value;
			}
		}
		for(int i = 0; i < len; i++)
			data[i] = mult[i];
	}
	public void mult(float... t)
	{
		for(int r = 0; r < dim; r++)
		{
			for(int c = 0; c < dim; c++)
			{
				float value = 0;
				for(int i = 0; i < dim; i++)
					value += data[r*dim + i]*t[i*dim + c];
				data[r*dim + c] = value;
			}
		}
	}
	/**
	 * dots the vector with the column of this matrix
	 * @param vec - the vector to dot
	 * @param col - the column to dot
	 * @returns - the dot product of the two vectors
	 */
	public float dot(float[] vec, int col)
	{
		float dot = 0;
		for(int i = 0; i < dim; i++)
			dot += vec[i]*data[i*dim + col];
		return dot;
	}
	/**
	 * dots the vector with the column of this matrix
	 * @param vec - the vector to dot
	 * @param col - the column to dot
	 * @returns - the dot product of the two vectors
	 */
	public float dot(Vec2d vec, int col)
	{
		return vec.x * data[col] + vec.y * data[dim + col];
	}
	/**
	 * dots the vector with the column of this matrix
	 * @param vec - the vector to dot
	 * @param col - the column to dot
	 * @returns - the dot product of the two vectors
	 */
	public float dot(Vec3d vec, int col)
	{
		return vec.x * data[col] + vec.y * data[dim + col] + vec.z * data[2*dim + col];
	}
	/**
	 * crosses the two vectors, only applicable for 3 dimensional transforms. First vector is the column, and the second one is the vector
	 * @param vec - the vector to cross
	 * @param col - the column to cross
	 * @returns the cross product of the two vectors
	 */
	public float[] cross(float[] vec, int col)
	{
		float[] cross = new float[3];
		cross[0] = data[dim + col]*vec[2] - data[dim*2 + col]*vec[1];
		cross[1] = data[dim*2 + col]*vec[0] - data[col]*vec[2];
		cross[2] = data[col]*vec[1] - data[dim + col]*vec[0];
		return cross;
	}
	/**
	 * crosses the two vectors, only applicable for 3 dimensional transforms. First vector is the column, and the second one is the vector
	 * @param vec - the vector to cross
	 * @param col - the column to cross
	 * @param product - stores the resulting cross product
	 * @returns the cross product of the two vectors
	 */
	public void cross(float[] vec, int col, float[] product)
	{
		product[0] = data[dim + col]*vec[2] - data[dim*2 + col]*vec[1];
		product[1] = data[dim*2 + col]*vec[0] - data[col]*vec[2];
		product[2] = data[col]*vec[1] - data[dim + col]*vec[0];
	}
	public void transpose()
	{
		for(int y = 0; y < dim; ++y)
			for(int x = 0; x < y; ++x)
			{
				float temp = data[y*dim+x];
				data[y*dim+x] = data[x*dim+y];
				data[x*dim+y] = temp;
			}
	}
	public void setRotationInstance(float angle)
	{
		float cos = (float) (Math.cos(angle));
		float sin = (float) (Math.sin(angle));
		set(0, 0, cos);
		set(1, 0, sin);
		set(0, 1, -sin);
		set(1, 1, cos);
	}
	public void setRotationInstance(Vec2d center, float angle)
	{
		float cos = (float) (Math.cos(angle));
		float sin = (float) (Math.sin(angle));
		set(0, 0, cos);
		set(1, 0, sin);
		set(0, 1, -sin);
		set(1, 1, cos);
		set(0, 2, (1-cos)*center.x + sin*center.y);
		set(1, 2, -sin*center.x + (1-cos)*center.y);
	}
	public void setRotationInstance(float x, float y, float angle)
	{
		float cos = (float) (Math.cos(angle));
		float sin = (float) (Math.sin(angle));
		set(0, 0, cos);
		set(1, 0, sin);
		set(0, 1, -sin);
		set(1, 1, cos);
		set(0, 2, (1-cos)*x + sin*y);
		set(1, 2, -sin*x + (1-cos)*y);
	}
	public void setAffineTransform(AffineTransform atx)
	{
		atx.setTransform(data[0], data[3], data[1], data[4], data[2], data[5]);
	}
	public void setAffineTransform(AffineTransform atx, float scale_x, float scale_y)
	{
		atx.setTransform(data[0] * scale_x, data[3] * scale_x, data[1] * scale_y, data[4] * scale_y, data[2], data[5]);
	}
	public void invert3x3(Transform inverse)
	{
		float det = data[0] * (data[4] * data[8] - data[5] * data[7]) -
				data[3] * (data[1] * data[8] - data[7] * data[2]) +
				data[6] * (data[1] * data[5] - data[4] * data[2]);

		float idet = 1.0f / det;
		inverse.data[0] = (data[4] * data[8] - data[5] * data[7]) * idet;
		inverse.data[3] = (data[6] * data[5] - data[3] * data[8]) * idet;
		inverse.data[6] = (data[3] * data[7] - data[6] * data[4]) * idet;
		inverse.data[1] = (data[7] * data[2] - data[1] * data[8]) * idet;
		inverse.data[4] = (data[0] * data[8] - data[6] * data[2]) * idet;
		inverse.data[7] = (data[1] * data[6] - data[0] * data[7]) * idet;
		inverse.data[2] = (data[1] * data[5] - data[2] * data[4]) * idet;
		inverse.data[5] = (data[2] * data[3] - data[0] * data[5]) * idet;
		inverse.data[8] = (data[0] * data[4] - data[1] * data[3]) * idet;
	}
	public void invert4x4(Transform inverse)
	{
		float[] inv = new float[16];
		float det;
	    int i;

	    inv[0] = data[5]  * data[10] * data[15] - 
	             data[5]  * data[11] * data[14] - 
	             data[9]  * data[6]  * data[15] + 
	             data[9]  * data[7]  * data[14] +
	             data[13] * data[6]  * data[11] - 
	             data[13] * data[7]  * data[10];

	    inv[4] = -data[4]  * data[10] * data[15] + 
	              data[4]  * data[11] * data[14] + 
	              data[8]  * data[6]  * data[15] - 
	              data[8]  * data[7]  * data[14] - 
	              data[12] * data[6]  * data[11] + 
	              data[12] * data[7]  * data[10];

	    inv[8] = data[4]  * data[9] * data[15] - 
	             data[4]  * data[11] * data[13] - 
	             data[8]  * data[5] * data[15] + 
	             data[8]  * data[7] * data[13] + 
	             data[12] * data[5] * data[11] - 
	             data[12] * data[7] * data[9];

	    inv[12] = -data[4]  * data[9] * data[14] + 
	               data[4]  * data[10] * data[13] +
	               data[8]  * data[5] * data[14] - 
	               data[8]  * data[6] * data[13] - 
	               data[12] * data[5] * data[10] + 
	               data[12] * data[6] * data[9];

	    inv[1] = -data[1]  * data[10] * data[15] + 
	              data[1]  * data[11] * data[14] + 
	              data[9]  * data[2] * data[15] - 
	              data[9]  * data[3] * data[14] - 
	              data[13] * data[2] * data[11] + 
	              data[13] * data[3] * data[10];

	    inv[5] = data[0]  * data[10] * data[15] - 
	             data[0]  * data[11] * data[14] - 
	             data[8]  * data[2] * data[15] + 
	             data[8]  * data[3] * data[14] + 
	             data[12] * data[2] * data[11] - 
	             data[12] * data[3] * data[10];

	    inv[9] = -data[0]  * data[9] * data[15] + 
	              data[0]  * data[11] * data[13] + 
	              data[8]  * data[1] * data[15] - 
	              data[8]  * data[3] * data[13] - 
	              data[12] * data[1] * data[11] + 
	              data[12] * data[3] * data[9];

	    inv[13] = data[0]  * data[9] * data[14] - 
	              data[0]  * data[10] * data[13] - 
	              data[8]  * data[1] * data[14] + 
	              data[8]  * data[2] * data[13] + 
	              data[12] * data[1] * data[10] - 
	              data[12] * data[2] * data[9];

	    inv[2] = data[1]  * data[6] * data[15] - 
	             data[1]  * data[7] * data[14] - 
	             data[5]  * data[2] * data[15] + 
	             data[5]  * data[3] * data[14] + 
	             data[13] * data[2] * data[7] - 
	             data[13] * data[3] * data[6];

	    inv[6] = -data[0]  * data[6] * data[15] + 
	              data[0]  * data[7] * data[14] + 
	              data[4]  * data[2] * data[15] - 
	              data[4]  * data[3] * data[14] - 
	              data[12] * data[2] * data[7] + 
	              data[12] * data[3] * data[6];

	    inv[10] = data[0]  * data[5] * data[15] - 
	              data[0]  * data[7] * data[13] - 
	              data[4]  * data[1] * data[15] + 
	              data[4]  * data[3] * data[13] + 
	              data[12] * data[1] * data[7] - 
	              data[12] * data[3] * data[5];

	    inv[14] = -data[0]  * data[5] * data[14] + 
	               data[0]  * data[6] * data[13] + 
	               data[4]  * data[1] * data[14] - 
	               data[4]  * data[2] * data[13] - 
	               data[12] * data[1] * data[6] + 
	               data[12] * data[2] * data[5];

	    inv[3] = -data[1] * data[6] * data[11] + 
	              data[1] * data[7] * data[10] + 
	              data[5] * data[2] * data[11] - 
	              data[5] * data[3] * data[10] - 
	              data[9] * data[2] * data[7] + 
	              data[9] * data[3] * data[6];

	    inv[7] = data[0] * data[6] * data[11] - 
	             data[0] * data[7] * data[10] - 
	             data[4] * data[2] * data[11] + 
	             data[4] * data[3] * data[10] + 
	             data[8] * data[2] * data[7] - 
	             data[8] * data[3] * data[6];

	    inv[11] = -data[0] * data[5] * data[11] + 
	               data[0] * data[7] * data[9] + 
	               data[4] * data[1] * data[11] - 
	               data[4] * data[3] * data[9] - 
	               data[8] * data[1] * data[7] + 
	               data[8] * data[3] * data[5];

	    inv[15] = data[0] * data[5] * data[10] - 
	              data[0] * data[6] * data[9] - 
	              data[4] * data[1] * data[10] + 
	              data[4] * data[2] * data[9] + 
	              data[8] * data[1] * data[6] - 
	              data[8] * data[2] * data[5];

	    det = data[0] * inv[0] + data[1] * inv[4] + data[2] * inv[8] + data[3] * inv[12];

	    det = 1.0f / det;

	    for (i = 0; i < 16; i++)
	        inverse.data[i] = inv[i] * det;
	}
	/**
	 * dot product of two vectors. calculated by summing the product of each index. ie: ax1*bx1 + ax2*bx2 + ...
	 * @param a - first vector
	 * @param b - second vector
	 * @returns the product.
	 */
	public static float dot(float[] a, float[] b)
	{
		float dot = 0;
		for(int i = 0; i < a.length; i++)
			dot += a[i]*b[i];
		return dot;
	}
	/**
	 * Multiplies Transforms A and B and adds the result to C. Transforms must be the correct dimensions, 
	 * or an out of bounds error will occur. The resulting value of C will be (A * B) + C
	 * @param A - Transform A
	 * @param B - Transform B
	 * @param C - Resulting Transform C
	 */
	public static void mult(Transform A, Transform B, Transform C)
	{
		float value = 0;
		for(int r = 0; r < A.dim; ++r)
		{
			for(int c = 0; c < A.dim; ++c)
			{
				value = 0;
				for(int i = 0; i < A.dim; i++)
					value += A.data[r*A.dim + i]*B.data[i*B.dim + c];
				C.data[r*C.dim + c] = value;
			}
		}
	}
	/**
	 * Gets the rotation matrix of a certain angle
	 * @param angle - the angle of the rotation
	 * @returns the rotation matrix
	 */
	public static Transform getRotationInstance(float angle)
	{
		Transform transform = new Transform(2, false);
		transform.set(0, 0, (float) (Math.cos(angle)));
		transform.set(1, 0, (float) (Math.sin(angle)));
		transform.set(0, 1, (float) (-Math.sin(angle)));
		transform.set(1, 1, (float) (Math.cos(angle)));
		return transform;
	}
	public static Transform getRotationInstance3D(float angle)
	{
		Transform transform = new Transform(3, true);
		transform.set(0, 0, (float) (Math.cos(angle)));
		transform.set(1, 0, (float) (Math.sin(angle)));
		transform.set(0, 1, (float) (-Math.sin(angle)));
		transform.set(1, 1, (float) (Math.cos(angle)));
		return transform;
	}
	public static float det2x2(float... data)
	{
		return data[0] * data[3] - data[2] * data[1];
	}
	public static float det3x3(float... data)
	{
		return data[0] * (data[4] * data[8] - data[5] * data[7]) -
			   data[3] * (data[1] * data[8] - data[7] * data[2]) +
			   data[6] * (data[1] * data[5] - data[4] * data[2]);
	}
	public static void main(String[] args)
	{
		Random r = new Random();
		Transform[] tfs = new Transform[4000];
		Transform product = new Transform(4, true);
		for(int i = 0; i < tfs.length; ++i)
		{
			tfs[i] = new Transform(4, true);
			for(int j = 0; j < tfs[i].data.length; ++j)
				tfs[i].data[j] = r.nextFloat();
		}
		
		long t1 = System.currentTimeMillis();
		for(int i = 0; i < tfs.length; ++i)
			for(int j = 0; j < tfs.length; ++j)
				Transform.mult(tfs[i], tfs[j], product);
		long t2 = System.currentTimeMillis();
		System.out.println("time: " + (t2 - t1));
	}
}

