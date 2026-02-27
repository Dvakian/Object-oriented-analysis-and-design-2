using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace lab01_code
{
    public partial class Form1 : Form
    {
        private BindingList<CarPartsRow> partsList = new BindingList<CarPartsRow>();
        private BaseFactory factory;

        public class CarPartsRow
        {
            public string EngineInfo { get; set; }
            public string WheelInfo { get; set; }
        }
        abstract class BaseFactory
        {
            public abstract BaseEngine createEngine();
            public abstract BaseWheel createWheel();
        }
        abstract class BaseEngine
        {
            public abstract string Name { get; }
            public abstract string Specs { get; }
        }
        abstract class BaseWheel
        {
            public abstract string Name { get; }
            public abstract string Specs { get; }
        }

        class HondaFactory : BaseFactory
        {
            public override BaseEngine createEngine()
            {
                return new HondaEngine();
            }
            public override BaseWheel createWheel()
            {
                return new HondaWheel();
            }
        }

        class ToyotaFactory : BaseFactory
        {
            public override BaseEngine createEngine()
            {
                return new ToyotaEngine();
            }
            public override BaseWheel createWheel()
            {
                return new ToyotaWheel();
            }
        }
        class ToyotaEngine : BaseEngine
        {

            public override string Name => "abobaAT";
            public override string Specs => "bbobaAT";
        }

        class HondaEngine : BaseEngine
        {
            public override string Name => "abobaAH";
            public override string Specs => "bbobaAH";
        }

        class ToyotaWheel : BaseWheel
        {

            public override string Name => "abobaBT";
            public override string Specs => "bbobaBT";
        }

        class HondaWheel : BaseWheel
        {
            public override string Name => "abobaBH";
            public override string Specs => "bbobaBH";
        }
        public Form1()
        {
            InitializeComponent();

            button1.Text = "Перейти";
            button2.Text = "Закрыть";

            dataGridView1.AutoGenerateColumns = false;
            dataGridView1.AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill;
            dataGridView1.Columns.Clear();

            dataGridView1.Columns.Add("Engine", "Двигатель");
            dataGridView1.Columns["Engine"].DataPropertyName = "EngineInfo";

            dataGridView1.Columns.Add("Wheel", "Калёса");
            dataGridView1.Columns["Wheel"].DataPropertyName = "WheelInfo";

            dataGridView1.DataSource = partsList;

            comboBox2.Items.Add("Toyota");
            comboBox2.Items.Add("Honda");
            comboBox2.Text = "Выберите производителя";
        }

        private void comboBox2_SelectedIndexChanged(object sender, EventArgs e)
        {
            partsList.Clear();
            string brand = comboBox2.SelectedItem.ToString();

            if (brand == "Honda")
                factory = new HondaFactory();
            else if (brand == "Toyota")
                factory = new ToyotaFactory();

            var engine = factory.createEngine();
            var wheel = factory.createWheel();

            partsList.Add(new CarPartsRow { EngineInfo = engine.Name, WheelInfo = wheel.Name });
            partsList.Add(new CarPartsRow { EngineInfo = engine.Specs, WheelInfo = wheel.Specs });
        }

        private void button1_Click(object sender, EventArgs e)
        {
            Form2 form2 = new Form2();
            form2.Show();
            this.Hide();
        }

        private void button2_Click(object sender, EventArgs e)
        {
            Application.Exit();
        }

        private void button3_Click(object sender, EventArgs e)
        {
            MessageBox.Show("Кнопка для заполнения пустоты в окне \nДо свидания");
        }

        private void Form1_closing(object sender, FormClosingEventArgs e)
        {
            Application.Exit();
        }
    }
}