public class Mahasiswa {
    private String nim;
    private String nama;
    private String jurusan;
    private int semester;
    
    public Mahasiswa(String nim, String nama, String jurusan, int semester) {
        this.nim = nim;
        this.nama = nama;
        this.jurusan = jurusan;
        this.semester = semester;
    }
    
    public String getNim() { return nim; }
    public void setNim(String nim) { this.nim = nim; }
    
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    
    public String getJurusan() { return jurusan; }
    public void setJurusan(String jurusan) { this.jurusan = jurusan; }
    
    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }
    
    // Format untuk tampilan tabel
    public String getFormattedData(int number) {
        return String.format("%-4d %-12s %-25s %-20s %-8d", 
                           number, nim, nama, jurusan, semester);
    }
}