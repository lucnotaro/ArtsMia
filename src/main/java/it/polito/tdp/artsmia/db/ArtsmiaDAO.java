package it.polito.tdp.artsmia.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.artsmia.model.ArtObject;
import it.polito.tdp.artsmia.model.EdgeModel;

public class ArtsmiaDAO {

	public List<ArtObject> listObjects() {
		
		String sql = "SELECT * from objects";
		List<ArtObject> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				ArtObject artObj = new ArtObject(res.getInt("object_id"), res.getString("classification"), res.getString("continent"), 
						res.getString("country"), res.getInt("curator_approved"), res.getString("dated"), res.getString("department"), 
						res.getString("medium"), res.getString("nationality"), res.getString("object_name"), res.getInt("restricted"), 
						res.getString("rights_type"), res.getString("role"), res.getString("room"), res.getString("style"), res.getString("title"));
				
				result.add(artObj);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			System.out.println("Errore in DAO!");
			e.printStackTrace();
			return null;
		}
	}
//database piccolo:	
	public Integer getWeigh(int sourceId,int targetId) {
		String sql="ELECT e1.object_id AS o1, e2.object_id AS o2, COUNT(*) AS n "
				+ "FROM exhibition_objects e1,exhibition_objects e2 "
				+ "WHERE e1.exhibition_id=e2.exhibition_id AND e1.object_id=? AND e2.object_id=?";
		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1,sourceId);
			st.setInt(2,targetId);
			ResultSet res = st.executeQuery();
			res.next();
			Integer peso=res.getInt("n");
			st.close();
			res.close();
			conn.close();
			return peso;
			
		} catch (SQLException e) {
			System.out.println("Errore in DAO!");
			e.printStackTrace();
			return null;
		}
	}
	//database grande:	
	public List<EdgeModel> getAllWeight(Map<Integer,ArtObject> idMap) {
		String sql = "SELECT e1.object_id AS o1, e2.object_id AS o2, COUNT(*) AS n "
				+ "FROM exhibition_objects e1,exhibition_objects e2 "
				+ "WHERE e1.exhibition_id=e2.exhibition_id AND e1.object_id>e2.object_id "
				+ "GROUP BY e1.object_id,e2.object_id "
				+ "ORDER BY n DESC";
		List<EdgeModel> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				int idSource=res.getInt("o1");
				int idTarget=res.getInt("o2");
				int peso=res.getInt("n");
				EdgeModel edge=new EdgeModel(idMap.get(idSource),idMap.get(idTarget),peso);
				result.add(edge);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			System.out.println("Errore in DAO!");
			e.printStackTrace();
			return null;
		}
	}
}
