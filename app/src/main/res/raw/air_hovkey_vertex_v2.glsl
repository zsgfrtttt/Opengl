uniform mat4 u_Matrix; // 4 * 4 矩阵

attribute vec4 a_Position;
attribute vec4 a_Color;

varying vec4 v_Color;

void main(){
    v_Color = a_Color;
    gl_Position =  a_Position * u_Matrix;
    gl_PointSize = 10.0;
}