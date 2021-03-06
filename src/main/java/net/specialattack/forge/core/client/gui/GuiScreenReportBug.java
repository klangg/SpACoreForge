package net.specialattack.forge.core.client.gui;

import java.io.IOException;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class GuiScreenReportBug extends GuiScreen {

    private GuiTextField title;
    private GuiTextBox content;

    public GuiScreenReportBug() {
        this.title = new GuiTextField(0, this.fontRendererObj, 0, 0, 256, 12);
        this.title.setMaxStringLength(40);
        this.content = new GuiTextBox(this.fontRendererObj, 0, 0, 256, 128);
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "Report a bug", this.width / 2, 15, 16777215);
        super.drawScreen(par1, par2, par3);
        this.title.drawTextBox();
        this.content.drawTextBox();
    }

    @Override
    protected void keyTyped(char par1, int par2) throws IOException {
        boolean flag = this.title.textboxKeyTyped(par1, par2);
        flag |= this.content.textboxKeyTyped(par1, par2);
        if (!flag) {
            super.keyTyped(par1, par2);
        }
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3) throws IOException {
        super.mouseClicked(par1, par2, par3);
        this.title.mouseClicked(par1, par2, par3);
        this.content.mouseClicked(par1, par2, par3);
    }

    @Override
    public void initGui() {
        super.initGui();
        Keyboard.enableRepeatEvents(true);
        int i = this.width / 2;
        int j = 32;

        String text = this.title.getText();
        this.title = new GuiTextField(0, this.fontRendererObj, i - 128, j, 256, 12);
        this.title.setMaxStringLength(40);
        this.title.setText(text);

        text = this.content.getText();
        this.content = new GuiTextBox(this.fontRendererObj, i - 128, j + 24, 256, 128);
        this.content.setText(text);
    }

}
