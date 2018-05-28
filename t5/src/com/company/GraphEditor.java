package com.company;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import sun.awt.Symbol;
import sun.security.provider.certpath.Vertex;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

// Implementar boolean que define se está clicando pela primeira vez ou segunda vez
// Se for primeira, salva o vertice num vertice global e click = true;
// Se for segunda, adiciona como vertice adjacente do primeiro e click = false;

// IMPLEMENTAR DESENHO DE LINHAS (MAIS UM BOTÃO) (SÓ PODE DESENHAR SE ESTIVER EM CIMA DE UM CÍRCULO (E SÓ PODE PARAR
// DE DESENHAR SE ESTIVER DENTRO DE OUTRO CÍRCULO)
// Conectar o primeiro vertex com o vertex de quando o botão do mouse foi solto (desde que não sejam iguais)

public class GraphEditor extends Application {

    Grafo grafo;
    String strColor;
    Color color;
    int shapeControler;
    //    0 when circle
    //    1 when square

    boolean click;
    Vertice vert;

    Pane pane;
    Rectangle rect;
    Circle c;
    Line line;

//    boolean drawTheLine = false;



    @Override
    public void start(Stage stage) {

        click = false;

        grafo = new Grafo();

        Button btnRed = new Button();
        Button btnGreen = new Button();
        Button btnBlue = new Button();

        btnRed.setMinSize(90, 30);
        btnGreen.setMinSize(90, 30);
        btnBlue.setMinSize(90, 30);

        btnRed.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
        btnGreen.setBackground(new Background(new BackgroundFill(Color.LIMEGREEN, null, null)));
        btnBlue.setBackground(new Background(new BackgroundFill(Color.BLUE, null, null)));

        btnRed.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                strColor = "#FF0000";
                color = Color.RED;
            }
        });
        btnGreen.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                strColor = "#32CD32";
                color = Color.LIMEGREEN;
            }
        });
        btnBlue.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                strColor = "#0000FF";
                color = Color.BLUE;
            }
        });

        Button btnShapeCircle = new Button("Circulo");
        Button btnShapeSquare = new Button("Quadrado");

        btnShapeCircle.setMinSize(90, 30);
        btnShapeSquare.setMinSize(90, 30);

        btnShapeCircle.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                shapeControler = 0;
            }
        });
        btnShapeSquare.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                shapeControler = 1;
            }
        });


        VBox vb = new VBox();
        vb.setSpacing(2);
        vb.setAlignment(Pos.CENTER);
        vb.getChildren().addAll(btnShapeCircle, btnShapeSquare, btnRed, btnGreen, btnBlue);

        pane = new Pane();


//      Cria um shape e um vértice novo
        pane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (shapeControler == 1) {
                    rect = new Rectangle(event.getX(),event.getY(),0,0);
                    rect.setFill(Paint.valueOf(strColor));
                    setRetangulo(rect);
                    grafo.addRectVertex(shapeControler, rect);
                    pane.getChildren().add(grafo.getLastRectVertex());
                } else if (shapeControler == 0){
                    c = new Circle(event.getX(), event.getY(), 0, color);
                    setCirculo(c);
                    grafo.addCircleVertex(shapeControler, c);
                    pane.getChildren().add(grafo.getLastCircleVertex());
                }
            }
        });
        pane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (shapeControler == 1) {
                    double dist = event.getX() - rect.getX();
                    if (dist < 0)
                        dist = 0;
                    if (dist > 60)
                        dist = 60;
                    rect.setWidth(dist);
                    rect.setHeight(dist);
                } else if (shapeControler == 0){
                    double dist = Math.abs(Math.hypot(event.getX(), event.getY()) - Math.hypot(c.getCenterX(), c.getCenterY()));
                    if (dist >= 30)
                        dist = 30;
                    c.setRadius(dist);
                }
            }
        });


        BorderPane bp = new BorderPane();
        bp.setRight(vb);
        bp.setCenter(pane);

        Scene scene = new Scene(bp, 800, 600);
        stage.setScene(scene);
        stage.show();
    }


//  Define se vai desenhar a linha ou não
    private void setCirculo (Circle circulo) {
        circulo.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (click == false) {
                    click = true;
                    vert = grafo.getVertexByShape(circulo);
                    vert.printConnections();
                } else if (click == true) {
                    Vertice vertiLocal = grafo.getVertexByShape(circulo);
                    if (!(vert == vertiLocal || vert.isConnected(vertiLocal))) {
                        click = false;
                        vert.connect(vertiLocal);
                        drawLine(vert, vertiLocal);
                    }
                }
            }
        });
    }


//  Define se vai desenhar a linha ou não
    public void setRetangulo (Rectangle retangulo) {
        retangulo.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (click == false) {
                    click = true;
                    vert = grafo.getVertexByShape(retangulo);
                    vert.printConnections();
                } else {
                    Vertice vertiLocal = grafo.getVertexByShape(retangulo);
                    if (!(vert == vertiLocal || vert.isConnected(vertiLocal))) {
                        click = false;
                        vert.connect(vertiLocal);
                        drawLine(vert, vertiLocal);
                    }
                }
            }
        });
    }

    public void drawLine (Vertice vert1, Vertice vert2) {
        int shape1 = vert1.getVertexShape();
        int shape2 = vert2.getVertexShape();

        if (shape1 == 0 && shape2 == 0) {
            Circle c1 = vert1.getCircle();
            Circle c2 = vert2.getCircle();

            line = new Line (c1.getCenterX(), c1.getCenterY(), c2.getCenterX(), c2.getCenterY());
            line.setStroke(Paint.valueOf(strColor));
            line.setStrokeWidth(3);
            pane.getChildren().add(line);
        }
        if (shape1 == 0 && shape2 == 1) {
            Circle c1 = vert1.getCircle();
            Rectangle r2 = vert2.getRect();

            line = new Line (c1.getCenterX(), c1.getCenterY(), r2.getX()+(r2.getWidth()/2), r2.getY()+(r2.getHeight()/2));
            line.setStroke(Paint.valueOf(strColor));
            line.setStrokeWidth(3);
            pane.getChildren().add(line);
        }
        if (shape1 == 1 && shape2 == 0) {
            Rectangle r1 = vert1.getRect();
            Circle c2 = vert2.getCircle();

            line = new Line (r1.getX()+(r1.getWidth()/2), r1.getY()+(r1.getHeight()/2), c2.getCenterX(), c2.getCenterY());
            line.setStroke(Paint.valueOf(strColor));
            line.setStrokeWidth(3);
            pane.getChildren().add(line);
        }
        if (shape1 == 1 && shape2 == 1) {
            Rectangle r1 = vert1.getRect();
            Rectangle r2 = vert2.getRect();

            line = new Line (r1.getX() + (r1.getWidth()/2), r1.getY()+(r1.getHeight()/2), r2.getX()+(r2.getWidth()/2), r2.getY()+(r2.getHeight()/2));
            line.setStroke(Paint.valueOf(strColor));
            line.setStrokeWidth(3);
            pane.getChildren().add(line);
        }
    }

    public static void main (String[] args) { launch(args); }
}