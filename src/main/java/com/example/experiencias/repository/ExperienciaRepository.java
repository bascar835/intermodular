package com.example.experiencias.repository;

import com.example.experiencias.db.DB;
import com.example.experiencias.dto.ExperienciaResumen;
import com.example.experiencias.entity.Experiencia;

import java.sql.Connection;
import java.util.List;

public class ExperienciaRepository {

    private final Connection con;

    public ExperienciaRepository(Connection con) {
        this.con = con;
    }

    // ── Todas las experiencias activas ───────────────────────────────
    public List<ExperienciaResumen> findAllActivas() {
        String sql = """
            SELECT e.id,
                   e.titulo,
                   e.descripcion,
                   e.precio,
                   e.ubicacion,
                   e.duracion_horas,
                   e.categoria_id,
                   c.nombre AS categoria_nombre
                   
            FROM experiencias e
            JOIN categorias c ON c.id = e.categoria_id
            
            ORDER BY c.nombre, e.titulo
            """;

        return DB.queryMany(con, sql, rs -> new ExperienciaResumen(
            rs.getInt("id"),
            rs.getString("titulo"),
            rs.getString("descripcion"),
            rs.getDouble("precio"),
            rs.getString("ubicacion"),
            rs.getInt("duracion_horas"),
            rs.getInt("categoria_id"),
            rs.getString("categoria_nombre")
            
        ));
    }

    // ── Filtrar por categoría ────────────────────────────────────────
    // El ? es parámetro preparado → protección contra inyección SQL
    public List<ExperienciaResumen> findPorCategoria(Long categoriaId) {
        String sql = """
            SELECT e.id,
                   e.titulo,
                   e.descripcion,
                   e.precio,
                   e.ubicacion,
                   e.duracion_horas,
                   e.categoria_id,
                   c.nombre AS categoria_nombre
                   
            FROM experiencias e
            JOIN categorias c ON c.id = e.categoria_id
            WHERE e.categoria_id = ?
            ORDER BY e.titulo
            """;

        return DB.queryMany(con, sql, rs -> new ExperienciaResumen(
            rs.getInt("id"),
            rs.getString("titulo"),
            rs.getString("descripcion"),
            rs.getDouble("precio"),
            rs.getString("ubicacion"),
            rs.getInt("duracion_horas"),
            rs.getInt("categoria_id"),
            rs.getString("categoria_nombre")
            
        ), categoriaId);
    }

    // ── Búsqueda por texto libre ─────────────────────────────────────
    // ILIKE = insensible a mayúsculas en PostgreSQL
    // Busca en titulo, descripcion y ubicacion
    public List<ExperienciaResumen> buscarPorTexto(String termino) {
        String sql = """
            SELECT e.id,
                   e.titulo,
                   e.descripcion,
                   e.precio,
                   e.ubicacion,
                   e.duracion_horas,
                   e.categoria_id,
                   c.nombre AS categoria_nombre
                   
            FROM experiencias e
            JOIN categorias c ON c.id = e.categoria_id
            WHERE 
               (
                   e.titulo      ILIKE '%' || ? || '%'
                OR e.descripcion ILIKE '%' || ? || '%'
                OR e.ubicacion   ILIKE '%' || ? || '%'
              )
            ORDER BY e.titulo
            """;

        // El término se pasa 3 veces, una por cada columna del WHERE
        return DB.queryMany(con, sql, rs -> new ExperienciaResumen(
            rs.getInt("id"),
            rs.getString("titulo"),
            rs.getString("descripcion"),
            rs.getDouble("precio"),
            rs.getString("ubicacion"),
            rs.getInt("duracion_horas"),
            rs.getInt("categoria_id"),
            rs.getString("categoria_nombre")
            
        ), termino, termino, termino);
    }

	public Experiencia find(int id) {
		String sql = """
		        SELECT id,
		               titulo,
		               descripcion,
		               precio,
		               ubicacion,
		               duracion_horas,
		               categoria_id
		               
		        FROM experiencias
		        WHERE id = ?
		        """;

		    return DB.queryOne(con, sql, rs -> new Experiencia(
		        rs.getInt("id"),
		        rs.getString("titulo"),
		        rs.getString("descripcion"),
		        rs.getDouble("precio"),
		        rs.getString("ubicacion"),
		        rs.getInt("duracion_horas"),
		        rs.getInt("categoria_id"),
		        null // fecha_creacion no se recupera en esta consulta
		    ));
	}

	public void insert(Experiencia experiencia) {
		// TODO Auto-generated method stub
		
	}

	public void update(Experiencia e) {
		 String sql = """
			        UPDATE experiencias
			        SET titulo = ?,
			            descripcion = ?,
			            precio = ?,
			            ubicacion = ?,
			            duracion_horas = ?,
			            categoria_id = ?
			            
			        WHERE id = ?
			        """;

			    DB.execute(con, sql,
			        e.getTitulo(),
			        e.getDescripcion(),
			        e.getPrecio(),
			        e.getUbicacion(),
			        e.getDuracion_horas(),
			        e.getCategoria_id(),
			        e.getId()
			    );
		
	}

	public void delete(int id) {
		// TODO Auto-generated method stub
		
	}
}
