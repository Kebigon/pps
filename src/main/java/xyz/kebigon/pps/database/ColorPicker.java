package xyz.kebigon.pps.database;

import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class ColorPicker
{
	private static final int BLACK = 0x000000;
	private static final int WHITE = 0xFFFFFF;

	Random random = new Random();

	public int pickBackgroundColor()
	{
		return random.nextInt(0x1000000);
	}

	public int pickForegroundColor(int bgColor)
	{
		final int r = (bgColor & 0xFF0000) >> 16;
		final int g = (bgColor & 0xFF00) >> 8;
		final int b = (bgColor & 0xFF);

		return (r + g + b) / 3 > 128 ? BLACK : WHITE;
	}
}
